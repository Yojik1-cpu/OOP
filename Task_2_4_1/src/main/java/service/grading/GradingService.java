package service.grading;

import dsl.ScriptData;
import model.Assignment;
import model.Checkpoint;
import model.GlobalSettings;
import model.LabTask;
import model.Student;
import model.StudentGroup;
import service.build.*;
import service.git.GitService;
import service.style.StyleCheckService;
import service.utils.ProcessUtils;
import service.utils.SimpleLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class GradingService {
    private final GitService gitService = new GitService();
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();
    private final List<BuildTool> buildTools = List.of(new GradleBuildTool(), new MavenBuildTool(), new JavacBuildTool());

    public FinalReport executeGrading(ScriptData data) {
        SimpleLogger.info("Verifying git config...");
        gitService.verifyGitConfig();
        Map<String, Student> studentsByNick = resolveStudentsFromGroups(data);
        Map<String, LabTask> tasksById = data.getTasks().stream().collect(Collectors.toMap(LabTask::getId, task -> task, (a, b) -> a));
        GlobalSettings settings = data.getGlobalSettings();
        
        List<StudentGradingResult> studentResults = new ArrayList<>();
        Map<String, List<Assignment>> assignmentsByStudent = data.getAssignments().stream().collect(Collectors.groupingBy(Assignment::getStudentGithubNick));
        SimpleLogger.info("Starting concurrent grading for " + assignmentsByStudent.size() + " students...");
        
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            List<Future<StudentGradingResult>> futures = new ArrayList<>();
            for (Map.Entry<String, List<Assignment>> entry : assignmentsByStudent.entrySet()) {
                Student student = studentsByNick.get(entry.getKey());
                futures.add(executor.submit(() -> processStudent(student, entry.getValue(), tasksById, settings)));
            }
            for (Future<StudentGradingResult> future : futures) {
                try {
                    studentResults.add(future.get());
                } catch (ExecutionException | InterruptedException e) {
                    SimpleLogger.error("Unhandled execution error", e.getCause());
                }
            }
        } finally {
            executor.shutdown();
        }
        
        SimpleLogger.info("Grading finished. Generating final report data...");
        return toFinalReport(studentResults, data, studentsByNick, tasksById, settings);
    }

    private StudentGradingResult processStudent(Student student, List<Assignment> assignments, Map<String, LabTask> tasksById, GlobalSettings settings) {
        StudentGradingResult studentResult = new StudentGradingResult();
        studentResult.studentNick = student != null ? student.getGithubNick() : null;
        
        if (student == null) {
            for (Assignment a : assignments) studentResult.assignments.add(AssignmentResult.error(a, "Student not found"));
            return studentResult;
        }
        if (student.getRepoUrl() == null || student.getRepoUrl().isBlank()) {
            for (Assignment a : assignments) studentResult.assignments.add(AssignmentResult.error(a, "Repo URL missing"));
            return studentResult;
        }
        try {
            Path tempRoot = Files.createTempDirectory("grading-" + student.getGithubNick() + "-");
            Path repoDir = tempRoot.resolve("repo");
            SimpleLogger.info("Cloning repository for student: " + student.getGithubNick() + " into " + tempRoot);
            boolean gitOk = gitService.cloneRepository(student.getRepoUrl(), repoDir, settings);
            
            if (gitOk) {
                studentResult.commitDates = gitService.getCommitDates(repoDir);
            }
            
            for (Assignment assignment : assignments) {
                AssignmentResult result = new AssignmentResult(assignment);
                result.gitOk = gitOk;
                if (!gitOk) {
                    result.error = "Git clone failed";
                    studentResult.assignments.add(result);
                    continue;
                }
                
                LabTask task = tasksById.get(assignment.getTaskId());
                if (task == null) {
                    result.error = "Task '" + assignment.getTaskId() + "' was not found in config.";
                    studentResult.assignments.add(result);
                    continue;
                }
                
                Path projectDir = resolveProjectDir(repoDir, assignment);
                if (projectDir == null) {
                    result.error = "Project path not found";
                    studentResult.assignments.add(result);
                    continue;
                }
                
                result.firstCommitDate = gitService.getFirstCommitDate(repoDir, repoDir.relativize(projectDir).toString());
                result.lastCommitDate = gitService.getLastCommitDate(repoDir, repoDir.relativize(projectDir).toString());
                
                BuildTool buildTool = buildTools.stream().filter(t -> t.canHandle(projectDir)).findFirst().orElse(null);
                if (buildTool == null) {
                    result.error = "No build tool found";
                    studentResult.assignments.add(result);
                    continue;
                }
                SimpleLogger.info("Checking student " + student.getGithubNick() + ", task " + task.getId());
                ProcessUtils.CommandResult compileRes = buildTool.compile(projectDir, settings);
                result.compilationOk = compileRes.exitCode == 0;
                if (!result.compilationOk) {
                    result.styleResult = new StyleCheckService.StyleResult(false, -1, "Compilation failed");
                    result.testStats = new TestStats(0, 0, 0, false, "Compilation failed", null);
                    result.finalScore = 0.0;
                    result.error = "Compilation failed: " + compileRes.output;
                    studentResult.assignments.add(result);
                    continue;
                }
                ProcessUtils.CommandResult docsRes = buildTool.generateDocs(projectDir, settings);
                result.docsOk = docsRes.exitCode == 0;
                result.styleResult = StyleCheckService.checkStyle(repoDir, projectDir);
                result.testStats = buildTool.runTests(projectDir, settings);
                result.finalScore = scoreCalculator.calculateScore(task, assignment, settings, result.lastCommitDate);
                studentResult.assignments.add(result);
            }
        } catch (IOException | InterruptedException e) {
            for (Assignment a : assignments) studentResult.assignments.add(AssignmentResult.error(a, "Internal error: " + e.getMessage()));
        }
        return studentResult;
    }

    private Path resolveProjectDir(Path repoDir, Assignment assignment) {
        String pathStr = assignment.getProjectPath();
        if (pathStr == null || pathStr.isBlank() || pathStr.equalsIgnoreCase("default")) pathStr = assignment.getTaskId();
        if (pathStr == null || pathStr.isBlank()) return repoDir;
        Path candidate = repoDir.resolve(pathStr).normalize();
        return Files.isDirectory(candidate) ? candidate : null;
    }

    private Map<String, Student> resolveStudentsFromGroups(ScriptData data) {
        Map<String, Student> map = new HashMap<>();
        for (StudentGroup group : data.getGroups()) {
            for (Student student : group.getStudents()) {
                if (student.getGithubNick() != null) map.put(student.getGithubNick(), student);
            }
        }
        return map;
    }

    private FinalReport toFinalReport(List<StudentGradingResult> studentResults, ScriptData data, Map<String, Student> studentsByNick, Map<String, LabTask> tasksById, GlobalSettings settings) {
        FinalReport report = new FinalReport();
        
        List<AssignmentResult> allResults = new ArrayList<>();
        for (StudentGradingResult sr : studentResults) {
            allResults.addAll(sr.assignments);
        }
        
        report.setTotalAssignments(allResults.size());
        int success = 0; double scoreSum = 0;
        for (AssignmentResult result : allResults) {
            if (result.error == null) success++;
            double score = result.finalScore == null ? 0.0 : result.finalScore;
            scoreSum += score;
            FinalReport.GradingRow row = new FinalReport.GradingRow();
            row.setStudentGithubNick(result.assignment.getStudentGithubNick());
            row.setTaskId(result.assignment.getTaskId());
            row.setGitOk(result.gitOk);
            row.setBuildOk(result.compilationOk);
            row.setDocsOk(result.docsOk);
            
            int styleWarnings = (result.styleResult != null) ? result.styleResult.errorsCount : -1;
            row.setStyleOk(styleWarnings >= 0 && styleWarnings < 10);
            row.setStyleErrors(styleWarnings);
            
            row.setTestsOk(result.testStats != null && result.testStats.commandSuccessful);
            row.setTotalTests(result.testStats != null ? result.testStats.total : 0);
            row.setFailedTests(result.testStats != null ? result.testStats.failed : 0);
            row.setSkippedTests(result.testStats != null ? result.testStats.skipped : 0);
            row.setPassedTests(result.testStats != null ? Math.max(0, result.testStats.total - result.testStats.failed - result.testStats.skipped) : 0);
            row.setCodeCoverage(result.testStats != null ? result.testStats.codeCoverage : null);
            row.setFinalScore(score);
            row.setFirstCommitDate(result.firstCommitDate);
            row.setLastCommitDate(result.lastCommitDate);
            
            if (result.error != null) row.setMessage(result.error.split("\n")[0]);
            else if (styleWarnings >= 10) row.setMessage("Checkstyle failed");
            else row.setMessage("OK");
            report.addRow(row);
        }
        report.setSuccessfulAssignments(success);
        report.setFailedAssignments(allResults.size() - success);
        report.setAverageScore(allResults.isEmpty() ? 0.0 : scoreSum / allResults.size());
        
        fillStudentSummaries(report, studentResults, data, studentsByNick, tasksById, settings);
        return report;
    }

    private void fillStudentSummaries(FinalReport report, List<StudentGradingResult> studentResults, ScriptData data, Map<String, Student> studentsByNick, Map<String, LabTask> tasksById, GlobalSettings settings) {
        List<Checkpoint> checkpoints = data.getCheckpoints().stream().sorted(Comparator.comparing(Checkpoint::getDate)).toList();
        
        int totalWeeks = 0;
        if (settings.getSemesterStart() != null && settings.getSemesterEnd() != null) {
            long days = ChronoUnit.DAYS.between(settings.getSemesterStart(), settings.getSemesterEnd());
            totalWeeks = (int) Math.ceil(days / 7.0);
        }

        for (StudentGradingResult sr : studentResults) {
            String nick = sr.studentNick;
            if (nick == null) continue;
            Student student = studentsByNick.get(nick);
            
            double total = sr.assignments.stream().mapToDouble(r -> r.finalScore == null ? 0.0 : r.finalScore).sum();
            int maxScore = sr.assignments.stream().map(r -> tasksById.get(r.assignment.getTaskId())).filter(java.util.Objects::nonNull).mapToInt(t -> t.getMaxScore() == null ? 100 : t.getMaxScore()).sum();
            
            int activeWeeks = 0;
            double activityPercentage = 0.0;
            if (totalWeeks > 0) {
                Set<Integer> activeWeekIndices = new HashSet<>();
                for (LocalDate date : sr.commitDates) {
                    if (!date.isBefore(settings.getSemesterStart()) && !date.isAfter(settings.getSemesterEnd())) {
                        long daysFromStart = ChronoUnit.DAYS.between(settings.getSemesterStart(), date);
                        activeWeekIndices.add((int) (daysFromStart / 7));
                    }
                }
                activeWeeks = activeWeekIndices.size();
                activityPercentage = (double) activeWeeks / totalWeeks;
            }

            double finalAdjustedScore = total;
            if (totalWeeks > 0) {
                double weight = settings.getActivityWeight() != null ? settings.getActivityWeight() : 0.2;
                finalAdjustedScore = total * (1.0 - weight) + total * weight * activityPercentage;
            }

            FinalReport.StudentSummary summary = new FinalReport.StudentSummary();
            summary.setGithubNick(nick); 
            summary.setFullName(student != null ? student.getFullName() : "-");
            summary.setTotalScore(finalAdjustedScore); 
            summary.setMaxScore(maxScore);
            summary.setActivityPercentage(activityPercentage);
            summary.setActiveWeeks(activeWeeks);
            summary.setTotalWeeks(totalWeeks);
            summary.setFinalGrade(toGrade(finalAdjustedScore, maxScore, settings));
            
            for (Checkpoint checkpoint : checkpoints) {
                List<AssignmentResult> cpAs = sr.assignments.stream().filter(r -> { LabTask t = tasksById.get(r.assignment.getTaskId()); return t != null && isBeforeOrEqual(t.getHardDeadline(), checkpoint.getDate()); }).toList();
                double cpScore = cpAs.stream().mapToDouble(r -> r.finalScore == null ? 0.0 : r.finalScore).sum();
                
                double cpAdjustedScore = cpScore;
                if (totalWeeks > 0) {
                    double weight = settings.getActivityWeight() != null ? settings.getActivityWeight() : 0.2;
                    cpAdjustedScore = cpScore * (1.0 - weight) + cpScore * weight * activityPercentage;
                }

                Integer req = checkpoint.getRequiredScore();
                if (req == null) req = cpAs.stream().map(r -> tasksById.get(r.assignment.getTaskId())).filter(java.util.Objects::nonNull).mapToInt(t -> t.getMaxScore() == null ? 100 : t.getMaxScore()).sum();
                FinalReport.CheckpointScore cp = new FinalReport.CheckpointScore();
                cp.setName(checkpoint.getName()); cp.setDate(checkpoint.getDate() == null ? "-" : checkpoint.getDate().toString());
                cp.setScore(cpAdjustedScore); cp.setGrade(toGrade(cpAdjustedScore, req, settings));
                summary.addCheckpoint(cp);
            }
            report.addStudentSummary(summary);
        }
    }

    private static boolean isBeforeOrEqual(LocalDate d, LocalDate cd) { return cd == null || (d != null && !d.isAfter(cd)); }

    private static String toGrade(double s, double ms, GlobalSettings set) {
        if (ms <= 0) return "N/A";
        double p = (s * 100.0) / ms;
        int e = Optional.ofNullable(set.getExcellentThreshold()).orElse(80);
        int g = Optional.ofNullable(set.getGoodThreshold()).orElse(60);
        int sa = Optional.ofNullable(set.getSatisfactoryThreshold()).orElse(40);
        if (p >= e) return "5"; if (p >= g) return "4"; if (p >= sa) return "3"; return "2";
    }

    public static class StudentGradingResult {
        public String studentNick;
        public List<AssignmentResult> assignments = new ArrayList<>();
        public List<LocalDate> commitDates = new ArrayList<>();
    }

    public static class AssignmentResult {
        public final Assignment assignment;
        public boolean gitOk;
        public boolean compilationOk;
        public boolean docsOk;
        public StyleCheckService.StyleResult styleResult;
        public TestStats testStats;
        public Double finalScore;
        public LocalDate firstCommitDate;
        public LocalDate lastCommitDate;
        public String error;
        public AssignmentResult(Assignment a) { this.assignment = a; }
        public static AssignmentResult error(Assignment a, String m) { AssignmentResult r = new AssignmentResult(a); r.error = m; return r; }
    }
}
