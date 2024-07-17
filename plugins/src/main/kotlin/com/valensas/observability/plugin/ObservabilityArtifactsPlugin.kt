package com.valensas.observability.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import java.nio.file.Path
import java.util.Properties

class ObservabilityArtifactsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("observabilityArtifacts") {
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val outputResourceDir =
                sourceSets
                    .named("main")
                    .get()
                    .output.resourcesDir!!
                    .toPath()
            it.doLast {
                outputResourceDir.toFile().mkdirs()
                writeDependencyVersions(project, outputResourceDir)
                writeProjectInfo(project, outputResourceDir)
            }
        }
    }

    private fun writeDependencyVersions(
        project: Project,
        outputResourceDir: Path
    ) {
        val dependencyProperties = Properties()
        project.configurations.forEach { configuration ->
            configuration.allDependencies.forEach { dependency ->
                if (dependency.group != null && dependency.version != null) {
                    val key = "${dependency.group}:${dependency.name}"
                    dependencyProperties[key] = dependency.version
                }
            }
        }
        val outputFile = File("$outputResourceDir/dependency-versions.properties")
        outputFile.writer().use { writer ->
            dependencyProperties.store(writer, null)
        }
    }

    private fun writeProjectInfo(
        project: Project,
        outputResourceDir: Path
    ) {
        val projectProperties = Properties()
        projectProperties["group"] = project.group
        projectProperties["version"] = project.version
        projectProperties["name"] = project.rootProject.name

        val projectOutputFile = File("$outputResourceDir/project-info.properties")
        projectOutputFile.writer().use { writer ->
            projectProperties.store(writer, null)
        }
    }
}
