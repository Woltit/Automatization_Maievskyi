package edu.automatization.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class CodeMergerTask extends DefaultTask {

    @InputFiles
    public abstract ConfigurableFileCollection getSourceDirs();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @TaskAction
    public void execute() throws IOException {
        getLogger().info("Починаємо злиття Java файлів...");

        File out = getOutputFile().get().getAsFile();
        Files.createDirectories(out.toPath().getParent());

        Files.writeString(out.toPath(), "/// Merged code ///\n\n",
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        for (File root : getSourceDirs().getFiles()) {
            if (root.exists()) {
                try (var paths = Files.walk(root.toPath())) {
                    paths.filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(".java"))
                            .forEach(this::appendFileContent);
                }
            }
        }
        getLogger().info("Files merged successfully to: " + out.getAbsolutePath());
    }

    private void appendFileContent(Path javaFile) {
        try {
            File out = getOutputFile().get().getAsFile();
            String content = "\n\n File: " + javaFile.getFileName() + " ---\n";
            content += Files.readString(javaFile, StandardCharsets.UTF_8);
            Files.writeString(out.toPath(), content, StandardOpenOption.APPEND);
        } catch (IOException e) {
            getLogger().error("Cannot read the file: " + javaFile, e);
        }
    }
}