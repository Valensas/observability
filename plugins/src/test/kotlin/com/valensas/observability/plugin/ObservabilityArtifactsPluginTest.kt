package com.valensas.observability.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File
import java.util.Properties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ObservabilityArtifactsPluginTest {
    @Test
    fun `registers observabilityArtifacts task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("com.valensas.observability-artifacts")

        assertNotNull(project.tasks.findByName("observabilityArtifacts"))
    }

    @Test
    fun `observabilityArtifacts task creates dependency-versions file`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("com.valensas.observability-artifacts")

        val task = project.tasks.findByName("observabilityArtifacts")!!
        task.actions.forEach { it.execute(task) }

        val outputDir = project.file("build/resources/main")
        assertTrue(File(outputDir, "dependency-versions.properties").exists())
    }

    @Test
    fun `observabilityArtifacts task creates project-info file with correct properties`() {
        val project =
            ProjectBuilder
                .builder()
                .withName("test-project")
                .build()
        project.group = "com.example"
        project.version = "1.0.0"
        project.plugins.apply("java")
        project.plugins.apply("com.valensas.observability-artifacts")

        val task = project.tasks.findByName("observabilityArtifacts")!!
        task.actions.forEach { it.execute(task) }

        val outputDir = project.file("build/resources/main")
        val props = Properties()
        File(outputDir, "project-info.properties").reader().use { props.load(it) }
        assertEquals("com.example", props["group"])
        assertEquals("1.0.0", props["version"])
        assertEquals("test-project", props["name"])
    }

    @Test
    fun `dependency-versions file contains project dependencies`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("com.valensas.observability-artifacts")
        project.dependencies.add("implementation", "org.example:some-lib:2.0.0")

        val task = project.tasks.findByName("observabilityArtifacts")!!
        task.actions.forEach { it.execute(task) }

        val outputDir = project.file("build/resources/main")
        val props = Properties()
        File(outputDir, "dependency-versions.properties").reader().use { props.load(it) }
        assertEquals("2.0.0", props["org.example:some-lib"])
    }

    @Test
    fun `does not apply git-properties without Spring Boot`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("com.valensas.observability-artifacts")

        assertTrue(!project.plugins.hasPlugin("com.gorylenko.gradle-git-properties"))
    }
}

class ObservabilityArtifactsPluginFunctionalTest {
    @Test
    fun `applies git-properties and configures buildInfo with Spring Boot`() {
        val projectDir =
            createTempProject(
                """
                plugins {
                    java
                    id("org.springframework.boot") version "4.0.5"
                    id("com.valensas.observability-artifacts")
                }

                repositories {
                    mavenCentral()
                }

                tasks.register("verifyPlugins") {
                    doLast {
                        val hasGitProps = project.plugins.hasPlugin("com.gorylenko.gradle-git-properties")
                        require(hasGitProps) { "git-properties plugin should be applied" }

                        val gitExt = project.extensions
                            .findByType(com.gorylenko.GitPropertiesPluginExtension::class.java)
                        requireNotNull(gitExt) { "gitProperties extension should exist" }
                        require(gitExt.keys == listOf(
                            "git.branch",
                            "git.commit.time",
                            "git.commit.id.abbrev",
                            "git.dirty",
                            "git.tags"
                        )) { "gitProperties keys not configured correctly, got: ${'$'}{gitExt.keys}" }

                        println("All plugin verifications passed")
                    }
                }
                """.trimIndent()
            )

        val result =
            GradleRunner
                .create()
                .withProjectDir(projectDir)
                .withArguments("verifyPlugins")
                .withPluginClasspath()
                .build()

        assertTrue(result.output.contains("All plugin verifications passed"))
        assertEquals(TaskOutcome.SUCCESS, result.task(":verifyPlugins")?.outcome)
    }

    @Test
    fun `applies git-properties when Spring Boot is applied after the plugin`() {
        val projectDir =
            createTempProject(
                """
                plugins {
                    java
                    id("com.valensas.observability-artifacts")
                    id("org.springframework.boot") version "4.0.5"
                }

                repositories {
                    mavenCentral()
                }

                tasks.register("verifyPlugins") {
                    doLast {
                        val hasGitProps = project.plugins.hasPlugin("com.gorylenko.gradle-git-properties")
                        require(hasGitProps) { "git-properties plugin should be applied" }
                        println("Plugin order verification passed")
                    }
                }
                """.trimIndent()
            )

        val result =
            GradleRunner
                .create()
                .withProjectDir(projectDir)
                .withArguments("verifyPlugins")
                .withPluginClasspath()
                .build()

        assertTrue(result.output.contains("Plugin order verification passed"))
    }

    @Test
    fun `does not apply git-properties without Spring Boot via GradleRunner`() {
        val projectDir =
            createTempProject(
                """
                plugins {
                    java
                    id("com.valensas.observability-artifacts")
                }

                tasks.register("verifyNoGitProps") {
                    doLast {
                        val hasGitProps = project.plugins.hasPlugin("com.gorylenko.gradle-git-properties")
                        require(!hasGitProps) { "git-properties should NOT be applied without Spring Boot" }
                        println("No git-properties verification passed")
                    }
                }
                """.trimIndent()
            )

        val result =
            GradleRunner
                .create()
                .withProjectDir(projectDir)
                .withArguments("verifyNoGitProps")
                .withPluginClasspath()
                .build()

        assertTrue(result.output.contains("No git-properties verification passed"))
    }

    private fun createTempProject(buildScript: String): File {
        val projectDir =
            File.createTempFile("gradle-test", "").apply {
                delete()
                mkdirs()
            }
        projectDir.resolve("settings.gradle.kts").writeText("")
        projectDir.resolve("build.gradle.kts").writeText(buildScript)
        projectDir.resolve(".git").mkdirs()
        return projectDir
    }
}
