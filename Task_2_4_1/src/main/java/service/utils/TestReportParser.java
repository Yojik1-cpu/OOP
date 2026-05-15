package service.utils;

import service.build.TestStats;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestReportParser {

    public static TestStats parseXmlReports(Path reportsDir, boolean commandSuccessful, String rawOutput, Double codeCoverage) {
        if (!Files.exists(reportsDir) || !Files.isDirectory(reportsDir)) {
            return new TestStats(0, commandSuccessful ? 0 : 1,
                    0, commandSuccessful, rawOutput, codeCoverage);
        }

        int total = 0;
        int failed = 0;
        int skipped = 0;
        boolean foundReports = false;

        try (Stream<Path> paths = Files.walk(reportsDir)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Iterable<Path> xmlFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".xml") && p.getFileName().toString().startsWith("TEST-"))
                    ::iterator;

            for (Path xmlFile : xmlFiles) {
                foundReports = true;
                try {
                    Document doc = builder.parse(xmlFile.toFile());
                    NodeList testsuites = doc.getElementsByTagName("testsuite");
                    for (int i = 0; i < testsuites.getLength(); i++) {
                        Element testsuite = (Element) testsuites.item(i);
                        total += getIntAttribute(testsuite, "tests");
                        failed += getIntAttribute(testsuite, "failures")
                                + getIntAttribute(testsuite, "errors");
                        skipped += getIntAttribute(testsuite, "skipped");
                    }
                } catch (Exception e) {
                    SimpleLogger.warn("Failed to parse test report " + xmlFile + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            SimpleLogger.warn("Error walking test reports directory " + reportsDir + ": " + e.getMessage());
        }

        if (!foundReports) {
            return new TestStats(0, commandSuccessful ? 0 : 1,
                    0, commandSuccessful, rawOutput, codeCoverage);
        }

        return new TestStats(total, failed, skipped, commandSuccessful, rawOutput, codeCoverage);
    }

    private static int getIntAttribute(Element element, String attrName) {
        String val = element.getAttribute(attrName);
        if (val == null || val.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
