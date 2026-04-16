import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.3.20"
    id("org.jmailen.kotlinter") version "5.4.2"
    id("maven-publish")
    id("java-library")
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin="java-library")
    apply(plugin="org.jetbrains.kotlin.jvm")
    apply(plugin="org.jmailen.kotlinter")

    group = "com.valensas"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    java.sourceCompatibility = JavaVersion.VERSION_17
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    dependencies {
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.getByName<Jar>("jar") {
        archiveClassifier = ""
    }

}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
