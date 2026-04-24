package service;

public class HtmlReportRenderer {
    public String render(FinalReport report) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><meta charset=\"UTF-8\">")
                .append("<title>OOP Grading Report</title>")
                .append("<style>")
                .append("body{font-family:Arial,sans-serif;margin:20px;}table{border-collapse:collapse;width:100%;margin-bottom:20px;}")
                .append("th,td{border:1px solid #ccc;padding:6px 8px;text-align:left;}th{background:#f5f5f5;}")
                .append("</style></head><body>");

        html.append("<h1>OOP Grading Report</h1>");
        html.append("<p>Total assignments: ").append(report.getTotalAssignments()).append("</p>");
        html.append("<p>Successful: ").append(report.getSuccessfulAssignments()).append("</p>");
        html.append("<p>Failed: ").append(report.getFailedAssignments()).append("</p>");
        html.append("<p>Average score: ").append(String.format("%.2f", report.getAverageScore())).append("</p>");

        html.append("<h2>Assignment Results</h2>");
        html.append("<table><thead><tr>")
                .append("<th>Student</th><th>Task</th><th>Git</th><th>Build</th><th>Docs</th><th>Style</th><th>Style Errors</th>")
                .append("<th>Tests</th><th>Passed</th><th>Failed</th><th>Skipped</th><th>Coverage</th><th>Score</th><th>Message</th>")
                .append("</tr></thead><tbody>");
        for (FinalReport.GradingRow row : report.getRows()) {
            
            String coverageCell;
            if (row.getTotalTests() > 0) {
                double coverage = (double) row.getPassedTests() / row.getTotalTests();
                String coverageText = String.format("%.0f%%", coverage * 100);
                if (coverage < 0.8) {
                    coverageCell = td(coverageText + " 🚨");
                } else {
                    coverageCell = td(coverageText);
                }
            } else {
                coverageCell = td("-");
            }

            html.append("<tr>")
                    .append(td(row.getStudentGithubNick()))
                    .append(td(row.getTaskId()))
                    .append(td(Boolean.toString(row.isGitOk())))
                    .append(td(Boolean.toString(row.isBuildOk())))
                    .append(td(Boolean.toString(row.isDocsOk())))
                    .append(td(Boolean.toString(row.isStyleOk())))
                    .append(td(Integer.toString(row.getStyleErrors())))
                    .append(td(Boolean.toString(row.isTestsOk())))
                    .append(td(Integer.toString(row.getPassedTests())))
                    .append(td(Integer.toString(row.getFailedTests())))
                    .append(td(Integer.toString(row.getSkippedTests())))
                    .append(coverageCell)
                    .append(td(String.format("%.1f", row.getFinalScore())))
                    .append(td(row.getMessage()))
                    .append("</tr>");
        }
        html.append("</tbody></table>");

        html.append("<h2>Checkpoint and Final Grades</h2>");
        html.append("<table><thead><tr><th>Student</th><th>Total</th><th>Max</th><th>Final Grade</th><th>Checkpoints</th></tr></thead><tbody>");
        for (FinalReport.StudentSummary summary : report.getStudentSummaries()) {
            StringBuilder cp = new StringBuilder();
            for (FinalReport.CheckpointScore checkpoint : summary.getCheckpoints()) {
                if (cp.length() > 0) {
                    cp.append("<br/>");
                }
                cp.append(escape(checkpoint.getName()))
                        .append(" (")
                        .append(escape(checkpoint.getDate()))
                        .append("): ")
                        .append(String.format("%.1f", checkpoint.getScore()))
                        .append(" / ")
                        .append(checkpoint.getGrade());
            }

            html.append("<tr>")
                    .append(td(summary.getGithubNick() + " (" + safe(summary.getFullName()) + ")"))
                    .append(td(String.format("%.1f", summary.getTotalScore())))
                    .append(td(Integer.toString(summary.getMaxScore())))
                    .append(td(summary.getFinalGrade()))
                    .append(td(cp.toString()))
                    .append("</tr>");
        }
        html.append("</tbody></table>");

        html.append("</body></html>");
        return html.toString();
    }

    private static String td(String value) {
        return "<td>" + escape(value) + "</td>";
    }

    private static String safe(String value) {
        return value == null ? "-" : value;
    }

    private static String escape(String value) {
        if (value == null) {
            return "-";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
