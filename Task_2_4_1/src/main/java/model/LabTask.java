package model;

import java.time.LocalDate;

public class LabTask {
    private String id;
    private String title;
    private String description;
    private Integer maxScore;
    private LocalDate softDeadline;
    private LocalDate hardDeadline;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public LocalDate getSoftDeadline() {
        return softDeadline;
    }

    public void setSoftDeadline(LocalDate softDeadline) {
        this.softDeadline = softDeadline;
    }

    public LocalDate getHardDeadline() {
        return hardDeadline;
    }

    public void setHardDeadline(LocalDate hardDeadline) {
        this.hardDeadline = hardDeadline;
    }

    @Override
    public String toString() {
        return "LabTask{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", maxScore=" + maxScore +
                ", softDeadline=" + softDeadline +
                ", hardDeadline=" + hardDeadline +
                '}';
    }
}
