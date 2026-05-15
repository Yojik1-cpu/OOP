package dsl;

import groovy.lang.Closure;
import model.Assignment;
import model.Checkpoint;
import model.GlobalSettings;
import model.LabTask;
import model.Student;
import model.StudentGroup;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Objects;

public class ConfigDslContext {
    private final ScriptData scriptData;
    private final ConfigLoader configLoader;
    private Path currentBaseDir;

    public ConfigDslContext(ScriptData scriptData, ConfigLoader configLoader, Path initialBaseDir) {
        this.scriptData = scriptData;
        this.configLoader = configLoader;
        this.currentBaseDir = initialBaseDir;
    }
    
    public void setCurrentBaseDir(Path currentBaseDir) {
        this.currentBaseDir = currentBaseDir;
    }

    public void task(Closure<?> closure) {
        LabTask task = new LabTask();
        TaskDsl delegate = new TaskDsl(task);
        runWithDelegate(closure, delegate);
        scriptData.getTasks().add(task);
    }

    public void group(Closure<?> closure) {
        StudentGroup group = new StudentGroup();
        GroupDsl delegate = new GroupDsl(group);
        runWithDelegate(closure, delegate);
        scriptData.getGroups().add(group);
    }

    public void assignment(Closure<?> closure) {
        Assignment assignment = new Assignment();
        AssignmentDsl delegate = new AssignmentDsl(assignment);
        runWithDelegate(closure, delegate);
        scriptData.getAssignments().add(assignment);
    }

    public void checkpoint(Closure<?> closure) {
        Checkpoint checkpoint = new Checkpoint();
        CheckpointDsl delegate = new CheckpointDsl(checkpoint);
        runWithDelegate(closure, delegate);
        scriptData.getCheckpoints().add(checkpoint);
    }

    public void globalSettings(Closure<?> closure) {
        GlobalSettingsDsl delegate = new GlobalSettingsDsl(scriptData.getGlobalSettings());
        runWithDelegate(closure, delegate);
    }

    public void importConfig(String path) {
        configLoader.importConfig(path, currentBaseDir);
    }

    private static void runWithDelegate(Closure<?> closure, Object delegate) {
        Closure<?> copy = (Closure<?>) closure.clone();
        copy.setDelegate(delegate);
        copy.setResolveStrategy(Closure.DELEGATE_FIRST);
        copy.call();
    }

    public static final class TaskDsl {
        private final LabTask task;
        public TaskDsl(LabTask task) { this.task = task; }
        public void id(String value) { task.setId(value); }
        public void title(String value) { task.setTitle(value); }
        public void maxScore(Number value) { task.setMaxScore(value == null ? null : value.intValue()); }
        public void softDeadline(String value) { task.setSoftDeadline(parseDate(value)); }
        public void hardDeadline(String value) { task.setHardDeadline(parseDate(value)); }
    }

    public static final class GroupDsl {
        private final StudentGroup group;
        public GroupDsl(StudentGroup group) { this.group = group; }
        public void name(String value) { group.setName(value); }
        public void student(Closure<?> closure) {
            Student student = new Student();
            StudentDsl delegate = new StudentDsl(student);
            runWithDelegate(closure, delegate);
            group.addStudent(student);
        }
    }

    public static final class StudentDsl {
        private final Student student;
        public StudentDsl(Student student) { this.student = student; }
        public void githubNick(String value) { student.setGithubNick(value); }
        public void fullName(String value) { student.setFullName(value); }
        public void repoUrl(String value) { student.setRepoUrl(value); }
    }

    public static final class AssignmentDsl {
        private final Assignment assignment;
        public AssignmentDsl(Assignment assignment) { this.assignment = assignment; }
        public void studentGithubNick(String value) { assignment.setStudentGithubNick(value); }
        public void taskId(String value) { assignment.setTaskId(value); }
        public void projectPath(String value) { assignment.setProjectPath(value); }
        public void score(Number value) { assignment.setScore(Objects.isNull(value) ? null : value.intValue()); }
        public void submittedAt(String value) { assignment.setSubmittedAt(parseDate(value)); }
    }

    public static final class CheckpointDsl {
        private final Checkpoint checkpoint;
        public CheckpointDsl(Checkpoint checkpoint) { this.checkpoint = checkpoint; }
        public void name(String value) { checkpoint.setName(value); }
        public void date(String value) { checkpoint.setDate(parseDate(value)); }
        public void requiredScore(Number value) { checkpoint.setRequiredScore(value == null ? null
                : value.intValue()); }
    }

    public static final class GlobalSettingsDsl {
        private final GlobalSettings settings;
        public GlobalSettingsDsl(GlobalSettings settings) { this.settings = settings; }
        public void testTimeout(Number value) { settings.setTestTimeoutSeconds(value == null ? null
                : value.longValue()); }
        public void buildTimeout(Number value) { settings.setBuildTimeoutSeconds(value == null ? null
                : value.longValue()); }
        public void gitTimeout(Number value) { settings.setGitTimeoutSeconds(value == null ? null
                : value.longValue()); }
        public void extraPoints(Number value) { settings.setExtraPoints(value == null ? null
                : value.intValue()); }
        public void excellentThreshold(Number value) { settings.setExcellentThreshold(value == null ? null
                : value.intValue()); }
        public void goodThreshold(Number value) { settings.setGoodThreshold(value == null ? null
                : value.intValue()); }
        public void satisfactoryThreshold(Number value) { settings.setSatisfactoryThreshold(value == null ? null
                : value.intValue()); }
        public void semesterStart(String value) { settings.setSemesterStart(parseDate(value)); }
        public void semesterEnd(String value) { settings.setSemesterEnd(parseDate(value)); }
        public void activityWeight(Number value) { settings.setActivityWeight(value == null ? null
                : value.doubleValue()); }
    }

    private static LocalDate parseDate(String value) {
        return value == null ? null : LocalDate.parse(value);
    }
}
