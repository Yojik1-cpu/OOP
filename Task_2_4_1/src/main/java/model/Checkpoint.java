package model;

import java.time.LocalDate;

public class Checkpoint {
    private String name;
    private LocalDate date;
    private Integer requiredScore;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getRequiredScore() {
        return requiredScore;
    }

    public void setRequiredScore(Integer requiredScore) {
        this.requiredScore = requiredScore;
    }

    @Override
    public String toString() {
        return "Checkpoint{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", requiredScore=" + requiredScore +
                '}';
    }
}
