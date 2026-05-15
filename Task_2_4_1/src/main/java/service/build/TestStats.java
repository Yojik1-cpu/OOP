package service.build;

public class TestStats {
    public final int total;
    public final int failed;
    public final int skipped;
    public final boolean commandSuccessful;
    public final String rawOutput;

    public final Double codeCoverage;

    public TestStats(int total, int failed, int skipped, boolean commandSuccessful,
                     String rawOutput, Double codeCoverage) {
        this.total = total;
        this.failed = failed;
        this.skipped = skipped;
        this.commandSuccessful = commandSuccessful;
        this.rawOutput = rawOutput;
        this.codeCoverage = codeCoverage;
    }

    @Override
    public String toString() {
        return "TestStats{" +
                "total=" + total +
                ", failed=" + failed +
                ", skipped=" + skipped +
                ", commandSuccessful=" + commandSuccessful +
                ", codeCoverage=" + codeCoverage +
                ", rawOutput='" + rawOutput + '\'' +
                '}';
    }
}
