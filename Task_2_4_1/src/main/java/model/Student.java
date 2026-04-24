package model;

public class Student {
    private String githubNick;
    private String fullName;
    private String repoUrl;

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

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    @Override
    public String toString() {
        return "Student{" +
                "githubNick='" + githubNick + '\'' +
                ", fullName='" + fullName + '\'' +
                ", repoUrl='" + repoUrl + '\'' +
                '}';
    }
}
