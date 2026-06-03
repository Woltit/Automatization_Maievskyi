import edu.automatization.plugin.ProjectUtilsPlugin

plugins {
    id("java")
    id("war")
}

group "edu.automatization"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


val junitVersion = "5.13.2"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

apply<ProjectUtilsPlugin>()

dependencies {
    compileOnly("jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0")
    compileOnly("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("backupSources") {
    group = "automatization"
    description = "Робить швидку копію папки src перед початком автоматизацій"

    doLast {
        val srcDir = file("src")
        val backupDir = layout.buildDirectory.dir("backup-sources").get().asFile
        if (srcDir.exists()) {
            srcDir.copyRecursively(backupDir, overwrite = true)
            println("Резервна копія папки src створена в: ${backupDir.path}")
        } else {
            println("Директорію src не знайдено.")
        }
    }
}