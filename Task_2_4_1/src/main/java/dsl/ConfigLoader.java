package dsl;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

public class ConfigLoader {
    private final Set<String> loadedFiles = new HashSet<>();
    private GroovyShell groovyShell;
    private ScriptData scriptData;
    private ConfigDslContext context;

    public ScriptData load(String resourcePath) {
        this.scriptData = new ScriptData();
        this.loadedFiles.clear();
        
        Binding binding = new Binding();
        this.context = new ConfigDslContext(scriptData, this);
        
        binding.setVariable("task", createClosure(c -> context.task(c)));
        binding.setVariable("group", createClosure(c -> context.group(c)));
        binding.setVariable("assignment", createClosure(c -> context.assignment(c)));
        binding.setVariable("checkpoint", createClosure(c -> context.checkpoint(c)));
        binding.setVariable("globalSettings", createClosure(c -> context.globalSettings(c)));
        binding.setVariable("importConfig", createStringClosure(s -> context.importConfig(s)));

        this.groovyShell = new GroovyShell(binding);
        
        executeFile(resourcePath);
        return scriptData;
    }

    public void importConfig(String resourcePath) {
        executeFile(resourcePath);
    }

    protected String readResource(String resourcePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            try (Scanner scanner = new Scanner(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read resource: " + resourcePath, e);
        }
    }

    private void executeFile(String resourcePath) {
        if (!loadedFiles.add(resourcePath)) {
            return;
        }
        
        String scriptContent = readResource(resourcePath);
        if (scriptContent == null) {
            throw new IllegalArgumentException("Config file not found in resources: " + resourcePath);
        }

        groovyShell.evaluate(scriptContent, resourcePath);
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
