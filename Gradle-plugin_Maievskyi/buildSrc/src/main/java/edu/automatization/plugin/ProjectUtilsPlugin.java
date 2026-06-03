package edu.automatization.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSetContainer;

public class ProjectUtilsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            var mainSourceDirs = sourceSets.getByName("main").getJava().getSourceDirectories();

            project.getTasks().register("merge", CodeMergerTask.class, task -> {
                task.setGroup("automatization");
                task.setDescription("Зливає вихідні файли Java в один файл.");

                task.getSourceDirs().from(mainSourceDirs);
                task.getOutputFile().set(project.getLayout().getBuildDirectory().file("merged-source.txt"));
            });

            project.getTasks().register("htmlReport", ReportGeneratorTask.class, task -> {
                task.setGroup("automatization");
                task.setDescription("Генерує HTML-звіт з кількістю файлів та рядків.");

                task.getSourceDirs().from(mainSourceDirs);
                task.getOutputFile().set(project.getLayout().getBuildDirectory().file("code-report.html"));
            });
        });
    }
}