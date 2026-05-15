package service.build;

import model.GlobalSettings;
import service.utils.ProcessUtils.CommandResult;

import java.io.IOException;
import java.nio.file.Path;

public interface BuildTool {
    boolean canHandle(Path projectDir);
    CommandResult compile(Path projectDir, GlobalSettings settings) throws IOException, InterruptedException;
    CommandResult generateDocs(Path projectDir, GlobalSettings settings) throws IOException, InterruptedException;
    TestStats runTests(Path projectDir, GlobalSettings settings) throws IOException, InterruptedException;
}
