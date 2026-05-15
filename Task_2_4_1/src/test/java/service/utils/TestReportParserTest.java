package service.utils;

import service.build.TestStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(2, stats.failed); // 1 failure + 1 error
        assertEquals(1, stats.skipped);
        assertEquals(0.8, stats.codeCoverage);
    }

    @Test
    public void testParseXmlReports_NoReports() {
        TestStats stats = TestReportParser.parseXmlReports(tempDir, true, "output", null);
        assertEquals(0, stats.total);
        assertEquals(0, stats.failed);
    }

    @Test
    public void testParseXmlReports_EmptyDir() throws IOException {
        Path emptyDir = tempDir.resolve("empty");
        Files.createDirectories(emptyDir);
        TestStats stats = TestReportParser.parseXmlReports(emptyDir, true, "output", null);
        assertEquals(0, stats.total);
    }
}
