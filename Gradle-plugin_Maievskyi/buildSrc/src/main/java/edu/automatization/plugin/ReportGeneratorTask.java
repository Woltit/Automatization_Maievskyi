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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class ReportGeneratorTask extends DefaultTask {

    @InputFiles
    public abstract ConfigurableFileCollection getSourceDirs();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @TaskAction
    public void execute() throws IOException {
        AtomicLong totalLines = new AtomicLong(0);
        AtomicInteger fileCount = new AtomicInteger(0);

        for (File root : getSourceDirs().getFiles()) {
            if (root.exists()) {
                try (var paths = Files.walk(root.toPath())) {
                    paths.filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(".java"))
                            .forEach(file -> {
                                fileCount.incrementAndGet();
                                try (var lines = Files.lines(file, StandardCharsets.UTF_8)) {
                                    totalLines.addAndGet(lines.count());
                                } catch (IOException e) {
                                    getLogger().error("Failed to read lines from: " + file, e);
                                }
                            });
                }
            }
        }

        String html = String.format("<html><body style='font-family: Arial;'><h2>Звіт по проєкту</h2>" +
                "<p>Кількість Java файлів: <b>%d</b></p>" +
                "<p>Загальна кількість рядків коду: <b>%d</b></p></body></html>", fileCount.get(), totalLines.get());

        File out = getOutputFile().get().getAsFile();
        Files.createDirectories(out.toPath().getParent());
        Files.writeString(out.toPath(), html, StandardCharsets.UTF_8);
        getLogger().info("HTML report is successfully: " + out.getAbsolutePath());
    }
}