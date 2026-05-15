package dsl;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ConfigLoader {
    private final Set<Path> loadedFiles = new HashSet<>();
    private GroovyShell groovyShell;
    private ScriptData scriptData;
    private ConfigDslContext context;

    public ScriptData load(Path configPath) {
        this.scriptData = new ScriptData();
        this.loadedFiles.clear();
        
        Path normalized = configPath.toAbsolutePath().normalize();
        
        Binding binding = new Binding();
        this.context = new ConfigDslContext(scriptData, this, normalized.getParent());
        
        binding.setVariable("task", createClosure(c -> context.task(c)));
        binding.setVariable("group", createClosure(c -> context.group(c)));
        binding.setVariable("assignment", createClosure(c -> context.assignment(c)));
        binding.setVariable("checkpoint", createClosure(c -> context.checkpoint(c)));
        binding.setVariable("globalSettings", createClosure(c -> context.globalSettings(c)));
        binding.setVariable("importConfig", createStringClosure(s -> context.importConfig(s)));

        this.groovyShell = new GroovyShell(binding);
        
        executeFile(normalized);
        return scriptData;
    }

    public void importConfig(String path, Path baseDir) {
        Path imported = baseDir.resolve(path).toAbsolutePath().normalize();
        executeFile(imported);
    }

    private void executeFile(Path path) {
        if (!loadedFiles.add(path)) {
            return;
        }
        File file = path.toFile();
        if (!file.exists()) {
            throw new IllegalArgumentException("Config file not found: " + path);
        }

        context.setCurrentBaseDir(path.getParent());

        try {
            groovyShell.evaluate(file);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to evaluate config: " + file.getAbsolutePath(), e);
        }
    }

    private Closure<?> createClosure(Consumer<Closure<?>> consumer) {
        return new Closure<Object>(this) {
            public Object doCall(Object... args) {
                if (args.length != 1 || !(args[0] instanceof Closure)) {
                    throw new IllegalArgumentException("Expected one closure argument");
                }
                consumer.accept((Closure<?>) args[0]);
                return null;
            }
        };
    }
    
    private Closure<?> createStringClosure(Consumer<String> consumer) {
        return new Closure<Object>(this) {
            public Object doCall(Object... args) {
                if (args.length != 1 || !(args[0] instanceof String)) {
                    throw new IllegalArgumentException("Expected one string argument");
                }
                consumer.accept((String) args[0]);
                return null;
            }
        };
    }
}
