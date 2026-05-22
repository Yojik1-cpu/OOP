package service.build;

import model.GlobalSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import service.utils.JacocoReportParser;
import service.utils.ProcessUtils;
import service.utils.TestReportParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

public class BuildToolsTest {

    @TempDir
    Path tempDir;

    @Test
    public void testGradleBuildToolCanHandle() throws IOException {
        GradleBuildTool tool = new GradleBuildTool();
        assertFalse(tool.canHandle(tempDir));
        Files.writeString(tempDir.resolve("build.gradle"), "");
        assertTrue(tool.canHandle(tempDir));
        Files.delete(tempDir.resolve("build.gradle"));
        Files.writeString(tempDir.resolve("build.gradle.kts"), "");
        assertTrue(tool.canHandle(tempDir));
    }

    @Test
    public void testGradleBuildToolCompile() throws IOException, InterruptedException {
        GradleBuildTool tool = new GradleBuildTool();
        ProcessUtils.CommandResult expectedResult = new ProcessUtils.CommandResult(0, "Success");
        
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(anyList(), eq(tempDir), anyLong()))
                    .thenReturn(expectedResult);

            ProcessUtils.CommandResult result = tool.compile(tempDir, new GlobalSettings());
            assertEquals(0, result.exitCode);
        }
    }

    @Test
    public void testGradleBuildToolRunTests() throws IOException, InterruptedException {
        GradleBuildTool tool = new GradleBuildTool();
        ProcessUtils.CommandResult expectedResult = new ProcessUtils.CommandResult(0, "Tests Passed");
        TestStats expectedStats = new TestStats(10, 0, 0, true, "Tests Passed", 85.0);
        
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class);
             MockedStatic<JacocoReportParser> mockedJacocoParser = Mockito.mockStatic(JacocoReportParser.class);
             MockedStatic<TestReportParser> mockedTestReportParser = Mockito.mockStatic(TestReportParser.class)) {
             
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(anyList(), eq(tempDir), anyLong()))
                    .thenReturn(expectedResult);
            mockedJacocoParser.when(() -> JacocoReportParser.parseCoverage(any(Path.class)))
                    .thenReturn(85.0);
            mockedTestReportParser.when(() -> TestReportParser.parseXmlReports(any(Path.class), anyBoolean(), any(String.class), anyDouble()))
                    .thenReturn(expectedStats);

            TestStats stats = tool.runTests(tempDir, new GlobalSettings());
            assertTrue(stats.commandSuccessful);
            assertEquals(10, stats.total);
            assertEquals(0, stats.failed);
            assertEquals(85.0, stats.codeCoverage);
        }
    }

    @Test
    public void testGradleBuildToolGenerateDocs() throws IOException, InterruptedException {
        GradleBuildTool tool = new GradleBuildTool();
        ProcessUtils.CommandResult expectedResult = new ProcessUtils.CommandResult(0, "Docs Generated");
        
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(anyList(), eq(tempDir), anyLong()))
                    .thenReturn(expectedResult);

            ProcessUtils.CommandResult result = tool.generateDocs(tempDir, new GlobalSettings());
            assertEquals(0, result.exitCode);
        }
    }

    @Test
    public void testMavenBuildToolCanHandle() throws IOException {
        MavenBuildTool tool = new MavenBuildTool();
        assertFalse(tool.canHandle(tempDir));
        Files.writeString(tempDir.resolve("pom.xml"), "");
        assertTrue(tool.canHandle(tempDir));
    }

    @Test
    public void testMavenBuildToolCompile() throws IOException, InterruptedException {
        MavenBuildTool tool = new MavenBuildTool();
        ProcessUtils.CommandResult expectedResult = new ProcessUtils.CommandResult(0, "Success");
        
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(anyList(), eq(tempDir), anyLong()))
                    .thenReturn(expectedResult);

            ProcessUtils.CommandResult result = tool.compile(tempDir, new GlobalSettings());
            assertEquals(0, result.exitCode);
        }
    }

    @Test
    public void testMavenBuildToolRunTests() throws IOException, InterruptedException {
        MavenBuildTool tool = new MavenBuildTool();
        ProcessUtils.CommandResult expectedResult = new ProcessUtils.CommandResult(0, "Tests Passed");
        TestStats expectedStats = new TestStats(20, 2, 1, true, "Tests Passed", 90.0);
        
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class);
             MockedStatic<JacocoReportParser> mockedJacocoParser = Mockito.mockStatic(JacocoReportParser.class);
             MockedStatic<TestReportParser> mockedTestReportParser = Mockito.mockStatic(TestReportParser.class)) {
             
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(anyList(), eq(tempDir), anyLong()))
                    .thenReturn(expectedResult);
            mockedJacocoParser.when(() -> JacocoReportParser.parseCoverage(any(Path.class)))
                    .thenReturn(90.0);
            mockedTestReportParser.when(() -> TestReportParser.parseXmlReports(any(Path.class), anyBoolean(), any(String.class), anyDouble()))
                    .thenReturn(expectedStats);

            TestStats stats = tool.runTests(tempDir, new GlobalSettings());
            assertTrue(stats.commandSuccessful);
            assertEquals(20, stats.total);
            assertEquals(2, stats.failed);
            assertEquals(90.0, stats.codeCoverage);
        }
    }

    @Test
    public void testMavenBuildToolGenerateDocs() throws IOException, InterruptedException {
        MavenBuildTool tool = new MavenBuildTool();
        ProcessUtils.CommandResult expectedResult = new ProcessUtils.CommandResult(0, "Docs Generated");
        
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(anyList(), eq(tempDir), anyLong()))
                    .thenReturn(expectedResult);

            ProcessUtils.CommandResult result = tool.generateDocs(tempDir, new GlobalSettings());
            assertEquals(0, result.exitCode);
        }
    }

    @Test
    public void testJavacBuildToolCanHandle() throws IOException {
        JavacBuildTool tool = new JavacBuildTool();
        assertFalse(tool.canHandle(tempDir));
        Path src = tempDir.resolve("src");
        Files.createDirectories(src);
        Files.writeString(src.resolve("A.java"), "class A{}");
        assertTrue(tool.canHandle(tempDir));
    }

    @Test
    public void testJavacBuildToolCompileAndDocs() throws IOException, InterruptedException {
        JavacBuildTool tool = new JavacBuildTool();
        Path src = tempDir.resolve("src/main/java");
        Files.createDirectories(src);
        Path javaFile = src.resolve("HelloWorld.java");
        Files.writeString(javaFile, "public class HelloWorld { public static void main(String[] args) {} }");

        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(anyList(), any(Path.class), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, "Success"));

            ProcessUtils.CommandResult compileResult = tool.compile(tempDir, new GlobalSettings());
            assertEquals(0, compileResult.exitCode);

            ProcessUtils.CommandResult docsResult = tool.generateDocs(tempDir, new GlobalSettings());
            assertEquals(0, docsResult.exitCode);
        }
    }

    @Test
    public void testJavacBuildToolRunTests() throws IOException, InterruptedException {
        JavacBuildTool tool = new JavacBuildTool();
        TestStats stats = tool.runTests(tempDir, new GlobalSettings());
        assertTrue(stats.commandSuccessful);
        assertEquals("No build tool for automated tests.", stats.rawOutput);
    }
}
