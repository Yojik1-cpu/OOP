package service.utils;

import service.build.TestStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TestReportParserTest {

    @TempDir
    Path tempDir;

    @Test
    public void testParseXmlReports_SumResults() throws IOException {
        Path report1 = tempDir.resolve("TEST-1.xml");
        Path report2 = tempDir.resolve("TEST-2.xml");
        
        String xml1 = "<testsuite tests=\"5\" failures=\"1\" errors=\"0\" skipped=\"1\"></testsuite>";
        String xml2 = "<testsuite tests=\"3\" failures=\"0\" errors=\"1\" skipped=\"0\"></testsuite>";
        
        Files.writeString(report1, xml1);
        Files.writeString(report2, xml2);

        TestStats stats = TestReportParser.parseXmlReports(tempDir, true, "output", 0.8);
        
        assertEquals(8, stats.total);
        assertEquals(2, stats.failed);
        assertEquals(1, stats.skipped);
        assertEquals(0.8, stats.codeCoverage);
        assertTrue(stats.commandSuccessful);
    }

    @Test
    public void testParseXmlReports_NoDirectory() {
        Path nonExistentDir = tempDir.resolve("nonexistent");
        TestStats stats = TestReportParser.parseXmlReports(nonExistentDir, true, "output", null);
        assertEquals(0, stats.total);
        assertEquals(0, stats.failed);
        assertEquals(0, stats.skipped);
    }

    @Test
    public void testParseXmlReports_NoXmlFiles() throws IOException {
        Files.writeString(tempDir.resolve("not-a-report.xml"), "<testsuite tests=\"1\"></testsuite>");
        TestStats stats = TestReportParser.parseXmlReports(tempDir, true, "output", null);
        assertEquals(0, stats.total);
        assertEquals(0, stats.failed);
    }

    @Test
    public void testParseXmlReports_InvalidAttributes() throws IOException {
        Path report = tempDir.resolve("TEST-invalid.xml");
        String xml = "<testsuite tests=\"foo\" failures=\"bar\" errors=\"baz\" skipped=\"qux\"></testsuite>";
        Files.writeString(report, xml);

        TestStats stats = TestReportParser.parseXmlReports(tempDir, true, "output", null);
        assertEquals(0, stats.total);
        assertEquals(0, stats.failed);
        assertEquals(0, stats.skipped);
    }

    @Test
    public void testParseXmlReports_MalformedXml() throws IOException {
        Path report = tempDir.resolve("TEST-malformed.xml");
        String xml = "<testsuite tests=\"1\">"; // Malformed XML
        Files.writeString(report, xml);

        TestStats stats = TestReportParser.parseXmlReports(tempDir, true, "output", null);
        assertEquals(0, stats.total);
        assertEquals(0, stats.failed);
    }

    @Test
    public void testParseXmlReports_CommandFailedNoReports() {
        TestStats stats = TestReportParser.parseXmlReports(tempDir, false, "Build Failed", null);
        assertEquals(0, stats.total);
        assertEquals(1, stats.failed); // Should be 1 because command failed
        assertFalse(stats.commandSuccessful);
        assertEquals("Build Failed", stats.rawOutput);
    }
}
