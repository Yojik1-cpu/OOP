package service.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JacocoReportParserTest {

    @TempDir
    Path tempDir;

    @Test
    public void testParseCoverage_ValidFile() throws IOException {
        Path reportFile = tempDir.resolve("jacoco.xml");
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                     "<report name=\"Task_1_1_1\">" +
                     "<counter type=\"INSTRUCTION\" missed=\"25\" covered=\"75\"/>" +
                     "<counter type=\"LINE\" missed=\"10\" covered=\"90\"/>" +
                     "</report>";
        Files.writeString(reportFile, xml);

        Double coverage = JacocoReportParser.parseCoverage(reportFile);
        assertEquals(0.75, coverage, 0.001);
    }

    @Test
    public void testParseCoverage_MissingFile() {
        assertNull(JacocoReportParser.parseCoverage(tempDir.resolve("non-existent.xml")));
    }

    @Test
    public void testParseCoverage_MalformedXml() throws IOException {
        Path reportFile = tempDir.resolve("malformed.xml");
        String xml = "<report><counter>...malformed";
        Files.writeString(reportFile, xml);
        assertNull(JacocoReportParser.parseCoverage(reportFile));
    }

    @Test
    public void testParseCoverage_NoCounters() throws IOException {
        Path reportFile = tempDir.resolve("nocounters.xml");
        String xml = "<report></report>";
        Files.writeString(reportFile, xml);
        assertNull(JacocoReportParser.parseCoverage(reportFile));
    }
}
