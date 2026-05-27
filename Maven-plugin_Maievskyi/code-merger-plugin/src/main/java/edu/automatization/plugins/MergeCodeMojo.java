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
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

@Mojo(name = "merge", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class MergeCodeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
    private List<String> compileSourceRoots;

    @Parameter(defaultValue = "${project.build.directory}/merged-source.txt")
    private File outputFile;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Починаємо злиття Java файлів...");

        try {
            Files.createDirectories(outputFile.toPath().getParent());

            Files.writeString(outputFile.toPath(), "/// Merged code ///\n\n",
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            for (String sourceRoot : compileSourceRoots) {
                Path rootPath = Path.of(sourceRoot);
                if (Files.exists(rootPath)) {
                    try (Stream<Path> paths = Files.walk(rootPath)) {
                        paths.filter(Files::isRegularFile)
                                .filter(p -> p.toString().endsWith(".java"))
                                .forEach(this::appendFileContent);
                    }
                }
            }
            getLog().info("Файли успішно об'єднані у: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("Помилка під час злиття файлів", e);
        }
    }

    private void appendFileContent(Path javaFile) {
        try {
            String content = "\n\n--- Файл: " + javaFile.getFileName() + " ---\n";
            content += Files.readString(javaFile);
            Files.writeString(outputFile.toPath(), content, StandardOpenOption.APPEND);
        } catch (IOException e) {
            getLog().error("Не вдалося прочитати файл: " + javaFile);
        }
    }
}