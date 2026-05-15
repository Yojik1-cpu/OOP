package model;

import java.time.LocalDate;

public class GlobalSettings {
    private Long testTimeoutSeconds = 300L;
    private Long buildTimeoutSeconds = 300L;
    private Long gitTimeoutSeconds = 180L;
    private Integer extraPoints = 0;
    private Integer excellentThreshold = 80;
    private Integer goodThreshold = 60;
    private Integer satisfactoryThreshold = 40;

    private LocalDate semesterStart;
    private LocalDate semesterEnd;
    private Double activityWeight = 0.2;

    public Long getTestTimeoutSeconds() {
        return testTimeoutSeconds;
    }

    public void setTestTimeoutSeconds(Long testTimeoutSeconds) {
        this.testTimeoutSeconds = testTimeoutSeconds;
    }

    public Long getBuildTimeoutSeconds() {
        return buildTimeoutSeconds;
    }

    public void setBuildTimeoutSeconds(Long buildTimeoutSeconds) {
        this.buildTimeoutSeconds = buildTimeoutSeconds;
    }

    public Long getGitTimeoutSeconds() {
        return gitTimeoutSeconds;
    }

    public void setGitTimeoutSeconds(Long gitTimeoutSeconds) {
        this.gitTimeoutSeconds = gitTimeoutSeconds;
    }

    public Integer getExtraPoints() {
        return extraPoints;
    }

    public void setExtraPoints(Integer extraPoints) {
        this.extraPoints = extraPoints;
    }

    public Integer getExcellentThreshold() {
        return excellentThreshold;
    }

    public void setExcellentThreshold(Integer excellentThreshold) {
        this.excellentThreshold = excellentThreshold;
    }

    public Integer getGoodThreshold() {
        return goodThreshold;
    }

    public void setGoodThreshold(Integer goodThreshold) {
        this.goodThreshold = goodThreshold;
    }

    public Integer getSatisfactoryThreshold() {
        return satisfactoryThreshold;
    }

    public void setSatisfactoryThreshold(Integer satisfactoryThreshold) {
        this.satisfactoryThreshold = satisfactoryThreshold;
    }

    public LocalDate getSemesterStart() {
        return semesterStart;
    }

    public void setSemesterStart(LocalDate semesterStart) {
        this.semesterStart = semesterStart;
    }

    public LocalDate getSemesterEnd() {
        return semesterEnd;
    }

    public void setSemesterEnd(LocalDate semesterEnd) {
        this.semesterEnd = semesterEnd;
    }

    public Double getActivityWeight() {
        return activityWeight;
    }

    public void setActivityWeight(Double activityWeight) {
        this.activityWeight = activityWeight;
    }

    @Override
    public String toString() {
        return "GlobalSettings{" +
                "testTimeoutSeconds=" + testTimeoutSeconds +
                ", buildTimeoutSeconds=" + buildTimeoutSeconds +
                ", gitTimeoutSeconds=" + gitTimeoutSeconds +
                ", extraPoints=" + extraPoints +
                ", excellentThreshold=" + excellentThreshold +
                ", goodThreshold=" + goodThreshold +
                ", satisfactoryThreshold=" + satisfactoryThreshold +
                ", semesterStart=" + semesterStart +
                ", semesterEnd=" + semesterEnd +
                ", activityWeight=" + activityWeight +
                '}';
    }
}
