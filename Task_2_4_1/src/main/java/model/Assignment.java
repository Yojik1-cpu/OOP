package model;

import java.time.LocalDate;

public class Assignment {
    private String studentGithubNick;
    private String taskId;
    private String projectPath;
    private Integer score;
    private String status;
    private LocalDate submittedAt;

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

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDate submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "studentGithubNick='" + studentGithubNick + '\'' +
                ", taskId='" + taskId + '\'' +
                ", projectPath='" + projectPath + '\'' +
                ", score=" + score +
                ", status='" + status + '\'' +
                ", submittedAt=" + submittedAt +
                '}';
    }
}
