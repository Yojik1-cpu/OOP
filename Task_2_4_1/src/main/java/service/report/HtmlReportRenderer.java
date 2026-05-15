package service.report;

import dsl.ScriptData;
import model.LabTask;
import model.Student;
import model.StudentGroup;
import service.grading.FinalReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HtmlReportRenderer {
    public String render(FinalReport report, ScriptData data) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html lang=\"en\"><head>")
                .append("<meta charset=\"UTF-8\">")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                .append("<title>OOP Grading Report</title>")
                .append("<style>")
                .append("body{font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;margin:40px;color:#333;background-color:#f9f9f9;}")
                .append("h1,h2,h3{color:#2c3e50;}")
                .append(".summary-box{background-color:#fff;padding:20px;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);margin-bottom:30px;}")
                .append(".summary-box p{margin:5px 0;font-size:16px;}")
                .append("table{border-collapse:collapse;width:100%;background-color:#fff;box-shadow:0 2px 4px rgba(0,0,0,0.1);border-radius:8px;overflow:hidden;margin-bottom:40px;}")
                .append("th,td{padding:8px 12px;text-align:left;border-bottom:1px solid #e0e0e0;}")
                .append("th{background-color:#34495e;color:#fff;font-weight:600;}")
                .append("tr:hover{background-color:#f5f5f5;}")
                .append(".true-cell{color:#27ae60;font-weight:bold;text-align:center;}")
                .append(".false-cell{color:#c0392b;font-weight:bold;text-align:center;}")
                .append(".dash-cell{color:#95a5a6;text-align:center;}")
                .append(".center-cell{text-align:center;}")
                .append(".nowrap-cell{white-space:nowrap;}")
                .append(".name-col{min-width:180px;white-space:nowrap;}")
                .append(".date-col{min-width:110px;white-space:nowrap;}")
                .append(".test-col{min-width:50px;text-align:center;white-space:nowrap;}")
                .append(".score-col{min-width:60px;text-align:center;white-space:nowrap;}")
                .append(".cov-col{min-width:80px;text-align:center;white-space:nowrap;}")
                .append(".msg-col{width:100%;}")
                .append("</style></head><body>");

        html.append("<h1>OOP Grading Report</h1>");
        html.append("<div class=\"summary-box\">");
        html.append("<p><strong>Total assignments processed:</strong> ").append(report.getTotalAssignments()).append("</p>");
        html.append("<p><strong>Successful checks:</strong> ").append(report.getSuccessfulAssignments()).append("</p>");
        html.append("<p><strong>Failed checks:</strong> ").append(report.getFailedAssignments()).append("</p>");
        html.append("</div>");

        html.append("<h2>Assignment Results by Task</h2>");
        List<Student> allStudents = new ArrayList<>();
        for (StudentGroup group : data.getGroups()) {
            allStudents.addAll(group.getStudents());
        }

        for (LabTask task : data.getTasks()) {
            html.append("<h3>Task: ").append(escape(task.getTitle())).append(" (").append(escape(task.getId())).append(")</h3>");
            html.append("<table><thead><tr>")
                    .append("<th class=\"name-col\">Student</th><th class=\"date-col\">First Commit</th><th class=\"date-col\">Last Commit</th>")
                    .append("<th class=\"test-col\">Build</th><th class=\"test-col\">Docs</th>")
                    .append("<th class=\"test-col\">Style</th><th class=\"test-col\">Style Warnings</th>")
                    .append("<th class=\"test-col\">Total Tests</th><th class=\"test-col\">Passed</th>")
                    .append("<th class=\"test-col\">Failed</th><th class=\"cov-col\">Coverage</th>")
                    .append("<th class=\"score-col\">Score</th><th class=\"msg-col\">Message</th>")
                    .append("</tr></thead><tbody>");
            for (Student student : allStudents) {
                String nick = student.getGithubNick();
                FinalReport.GradingRow row = findRow(report, nick, task.getId());
                if (row != null) {
                    String coverageCell;
                    if (row.getCodeCoverage() != null) {
                        double coverage = row.getCodeCoverage();
                        String coverageText = String.format(Locale.US, "%.0f%%", coverage * 100);
                        if (coverage < 0.8) {
                            coverageCell = "<td class=\"cov-col\">" + coverageText + " 🚨</td>";
                        } else {
                            coverageCell = "<td class=\"cov-col\">" + coverageText + "</td>";
                        }
                    } else if (row.getTotalTests() > 0) {
                        coverageCell = "<td class=\"cov-col\">No Report</td>";
                    } else {
                        coverageCell = "<td class=\"cov-col dash-cell\">-</td>";
                    }

                    html.append("<tr>")
                            .append("<td class=\"name-col\">").append(escape(row.getStudentGithubNick())).append("</td>")
                            .append("<td class=\"date-col\">").append(escape(row.getFirstCommitDate() != null ? row.getFirstCommitDate().toString() : "-")).append("</td>")
                            .append("<td class=\"date-col\">").append(escape(row.getLastCommitDate() != null ? row.getLastCommitDate().toString() : "-")).append("</td>")
                            .append(formatBool(row.isBuildOk(), "test-col"))
                            .append(formatBool(row.isDocsOk(), "test-col"))
                            .append(formatBool(row.isStyleOk(), "test-col"))
                            .append("<td class=\"test-col\">").append(row.getStyleErrors() >= 0 ? Integer.toString(row.getStyleErrors()) : "-").append("</td>")
                            .append("<td class=\"test-col\">").append(row.getTotalTests()).append("</td>")
                            .append("<td class=\"test-col\">").append(row.getPassedTests()).append("</td>")
                            .append("<td class=\"test-col\">").append(row.getFailedTests()).append("</td>")
                            .append(coverageCell)
                            .append("<td class=\"score-col\">").append(String.format(Locale.US, "%.1f", row.getFinalScore())).append("</td>")
                            .append("<td class=\"msg-col\">").append(escape(row.getMessage())).append("</td>")
                            .append("</tr>");
                } else {
                    html.append("<tr>")
                            .append("<td class=\"name-col\">").append(escape(nick)).append("</td>")
                            .append("<td class=\"date-col dash-cell\">-</td>")
                            .append("<td class=\"date-col dash-cell\">-</td>")
                            .append("<td class=\"test-col dash-cell\">-</td>")
                            .append("<td class=\"test-col dash-cell\">-</td>")
                            .append("<td class=\"test-col dash-cell\">-</td>")
                            .append("<td class=\"test-col dash-cell\">-</td>")
                            .append("<td class=\"test-col dash-cell\">-</td>")
                            .append("<td class=\"test-col dash-cell\">-</td>")
                            .append("<td class=\"test-col dash-cell\">-</td>")
                            .append("<td class=\"cov-col dash-cell\">-</td>")
                            .append("<td class=\"score-col\">0.0</td>")
                            .append("<td class=\"msg-col\">Not submitted</td>")
                            .append("</tr>");
                }
            }
            html.append("</tbody></table>");
        }

        html.append("<h2>Checkpoint and Final Grades</h2>");
        html.append("<table><thead><tr>")
                .append("<th>Student</th><th class=\"test-col\">Activity</th>")
                .append("<th class=\"score-col\">Total Score</th><th class=\"score-col\">Max Score</th>")
                .append("<th class=\"score-col\">Final Grade</th><th class=\"msg-col\">Checkpoints</th>")
                .append("</tr></thead><tbody>");
        for (FinalReport.StudentSummary summary : report.getStudentSummaries()) {
            StringBuilder cp = new StringBuilder();
            for (FinalReport.CheckpointScore checkpoint : summary.getCheckpoints()) {
                if (!cp.isEmpty()) cp.append("<br/>");
                cp.append("<strong>").append(escape(checkpoint.getName())).append("</strong>")
                        .append(" (").append(escape(checkpoint.getDate())).append("): ")
                        .append(String.format(Locale.US, "%.1f", checkpoint.getScore()))
                        .append(" / ").append(escape(checkpoint.getGrade()));
            }

            String activityText;
            if (summary.getTotalWeeks() > 0) {
                activityText = String.format(Locale.US, "%d/%d (%.0f%%)", summary.getActiveWeeks(), summary.getTotalWeeks(), summary.getActivityPercentage() * 100);
            } else {
                activityText = "-";
            }

            html.append("<tr>")
                    .append("<td class=\"name-col\">").append(escape(summary.getGithubNick() + " (" + safe(summary.getFullName()) + ")")).append("</td>")
                    .append("<td class=\"test-col\">").append(escape(activityText)).append("</td>")
                    .append("<td class=\"score-col\">").append(String.format(Locale.US, "%.1f", summary.getTotalScore())).append("</td>")
                    .append("<td class=\"score-col\">").append(summary.getMaxScore()).append("</td>")
                    .append("<td class=\"score-col\"><strong>").append(escape(summary.getFinalGrade())).append("</strong></td>")
                    .append("<td class=\"msg-col\">").append(cp.toString()).append("</td>")
                    .append("</tr>");
        }
        html.append("</tbody></table></body></html>");
        return html.toString();
    }

    private FinalReport.GradingRow findRow(FinalReport report, String studentNick, String taskId) {
        for (FinalReport.GradingRow row : report.getRows()) {
            if (row.getStudentGithubNick().equals(studentNick) && row.getTaskId().equals(taskId)) return row;
        }
        return null;
    }

    private static String formatBool(boolean value, String baseClass) {
        if (value) {
            return "<td class=\"" + baseClass + " true-cell\">true</td>";
        } else {
            return "<td class=\"" + baseClass + " false-cell\">false</td>";
        }
    }

    private static String safe(String value) {
        return value == null ? "-" : value;
    }

    private static String escape(String value) {
        if (value == null) return "-";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
