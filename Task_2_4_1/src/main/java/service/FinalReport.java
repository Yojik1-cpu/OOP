package service;

import java.util.ArrayList;
import java.util.List;

public class FinalReport {
    private int totalAssignments;
    private int successfulAssignments;
    private int failedAssignments;
    private double averageScore;
    private final List<GradingRow> rows = new ArrayList<>();
    private final List<StudentSummary> studentSummaries = new ArrayList<>();

    public int getTotalAssignments() {
        return totalAssignments;
    }

    public void setTotalAssignments(int totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public int getSuccessfulAssignments() {
        return successfulAssignments;
    }

    public void setSuccessfulAssignments(int successfulAssignments) {
        this.successfulAssignments = successfulAssignments;
    }

    public int getFailedAssignments() {
        return failedAssignments;
    }

    public void setFailedAssignments(int failedAssignments) {
        this.failedAssignments = failedAssignments;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public List<GradingRow> getRows() {
        return rows;
    }

    public void addRow(GradingRow row) {
        rows.add(row);
    }

    public List<StudentSummary> getStudentSummaries() {
        return studentSummaries;
    }

    public void addStudentSummary(StudentSummary summary) {
        studentSummaries.add(summary);
    }

    public static class GradingRow {
        private String studentGithubNick;
        private String taskId;
        private boolean gitOk;
        private boolean buildOk;
        private boolean testsOk;
        private boolean styleOk;
        private int styleErrors;
        private boolean docsOk;
        private int totalTests;
        private int failedTests;
        private int skippedTests;
        private int passedTests;
        private double finalScore;
        private String message;

        public String getStudentGithubNick() {
            return studentGithubNick;
        }

        public void setStudentGithubNick(String studentGithubNick) {
            this.studentGithubNick = studentGithubNick;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public boolean isGitOk() {
            return gitOk;
        }

        public void setGitOk(boolean gitOk) {
            this.gitOk = gitOk;
        }

        public boolean isBuildOk() {
            return buildOk;
        }

        public void setBuildOk(boolean buildOk) {
            this.buildOk = buildOk;
        }

        public boolean isTestsOk() {
            return testsOk;
        }

        public void setTestsOk(boolean testsOk) {
            this.testsOk = testsOk;
        }

        public boolean isStyleOk() {
            return styleOk;
        }

        public void setStyleOk(boolean styleOk) {
            this.styleOk = styleOk;
        }
        
        public int getStyleErrors() {
            return styleErrors;
        }

        public void setStyleErrors(int styleErrors) {
            this.styleErrors = styleErrors;
        }

        public boolean isDocsOk() {
            return docsOk;
        }

        public void setDocsOk(boolean docsOk) {
            this.docsOk = docsOk;
        }

        public int getTotalTests() {
            return totalTests;
        }

        public void setTotalTests(int totalTests) {
            this.totalTests = totalTests;
        }

        public int getFailedTests() {
            return failedTests;
        }

        public void setFailedTests(int failedTests) {
            this.failedTests = failedTests;
        }

        public double getFinalScore() {
            return finalScore;
        }

        public int getSkippedTests() {
            return skippedTests;
        }

        public void setSkippedTests(int skippedTests) {
            this.skippedTests = skippedTests;
        }

        public int getPassedTests() {
            return passedTests;
        }

        public void setPassedTests(int passedTests) {
            this.passedTests = passedTests;
        }

        public void setFinalScore(double finalScore) {
            this.finalScore = finalScore;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class StudentSummary {
        private String githubNick;
        private String fullName;
        private double totalScore;
        private int maxScore;
        private String finalGrade;
        private final List<CheckpointScore> checkpoints = new ArrayList<>();

        public String getGithubNick() {
            return githubNick;
        }

        public void setGithubNick(String githubNick) {
            this.githubNick = githubNick;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public double getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(double totalScore) {
            this.totalScore = totalScore;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(int maxScore) {
            this.maxScore = maxScore;
        }

        public String getFinalGrade() {
            return finalGrade;
        }

        public void setFinalGrade(String finalGrade) {
            this.finalGrade = finalGrade;
        }

        public List<CheckpointScore> getCheckpoints() {
            return checkpoints;
        }

        public void addCheckpoint(CheckpointScore checkpointScore) {
            checkpoints.add(checkpointScore);
        }
    }

    public static class CheckpointScore {
        private String name;
        private String date;
        private double score;
        private String grade;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }
    }
}
