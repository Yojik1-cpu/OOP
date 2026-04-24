package model;

public class GlobalSettings {
    private String gradingCriteria = "default";
    private Long testTimeoutSeconds = 300L;
    private Long buildTimeoutSeconds = 300L;
    private Long gitTimeoutSeconds = 180L;
    private Integer extraPoints = 0;
    private Double latePenaltyPerDay = 0.1;
    private Integer excellentThreshold = 85;
    private Integer goodThreshold = 70;
    private Integer satisfactoryThreshold = 50;

    public String getGradingCriteria() {
        return gradingCriteria;
    }

    public void setGradingCriteria(String gradingCriteria) {
        this.gradingCriteria = gradingCriteria;
    }

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

    public Double getLatePenaltyPerDay() {
        return latePenaltyPerDay;
    }

    public void setLatePenaltyPerDay(Double latePenaltyPerDay) {
        this.latePenaltyPerDay = latePenaltyPerDay;
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

    @Override
    public String toString() {
        return "GlobalSettings{" +
                "gradingCriteria='" + gradingCriteria + '\'' +
                "testTimeoutSeconds=" + testTimeoutSeconds +
                ", buildTimeoutSeconds=" + buildTimeoutSeconds +
                ", gitTimeoutSeconds=" + gitTimeoutSeconds +
                ", extraPoints=" + extraPoints +
                ", latePenaltyPerDay=" + latePenaltyPerDay +
                ", excellentThreshold=" + excellentThreshold +
                ", goodThreshold=" + goodThreshold +
                ", satisfactoryThreshold=" + satisfactoryThreshold +
                '}';
    }
}
