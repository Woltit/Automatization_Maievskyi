package edu.automatization.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Mojo(name = "html-report", defaultPhase = LifecyclePhase.VERIFY)
public class GenerateReportMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
    private List<String> compileSourceRoots;

    @Parameter(defaultValue = "${project.build.directory}/code-report.html")
    private File outputFile;

    @Override
    public void execute() throws MojoExecutionException {
        long totalLines = 0;
        int fileCount = 0;

        for (String sourceRoot : compileSourceRoots) {
            Path rootPath = Path.of(sourceRoot);
            if (Files.exists(rootPath)) {
                try (Stream<Path> paths = Files.walk(rootPath)) {
                    List<Path> javaFiles = paths.filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(".java"))
                            .toList();
                    for (Path file : javaFiles) {
                        fileCount++;
                        totalLines += Files.lines(file).count();
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException("Failed to read the file", e);
                }
            }
        }

        String html = String.format("<html><body style='font-family: Arial;'><h2>Звіт по проєкту</h2>" +
                "<p>Кількість Java файлів: <b>%d</b></p>" +
                "<p>Загальна кількість рядків коду: <b>%d</b></p></body></html>", fileCount, totalLines);

        try {
            Files.createDirectories(outputFile.toPath().getParent());
            Files.writeString(outputFile.toPath(), html);
            getLog().info("HTML report is successfully: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to create the report", e);
        }
    }
}