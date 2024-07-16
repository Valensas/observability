package com.valensas.observability.plugin

import org.gradle.testfixtures.ProjectBuilder
import java.io.File
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ObservabilityArtifactsPluginTest {
    @Test
    fun `Plugin registers and successfully executes task to create dependency versions file`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("com.valensas.observability-artifacts")

        val observabilityArtifactsPlugin = project.tasks.findByName("observabilityArtifacts")
        assertNotNull(observabilityArtifactsPlugin)

        observabilityArtifactsPlugin.actions.forEach { action -> action.execute(observabilityArtifactsPlugin) }

        val outputDir = project.file("build/resources/main")
        val dependencyVersionsFile = File(outputDir, "dependency-versions.properties")
        val projectInfoFile = File(outputDir, "project-info.properties")
        assertTrue(dependencyVersionsFile.exists())
        assertTrue(projectInfoFile.exists())
    }
}
