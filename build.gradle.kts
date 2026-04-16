import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.github.ben-manes.versions") version "0.52.0"
    kotlin("jvm") version "2.0.0"
    id("org.jmailen.kotlinter") version "5.0.1"
    id("maven-publish")
    id("java-library")
}


dependencies {
    compileOnly("org.springframework.boot:spring-boot-gradle-plugin:4.0.5")
    implementation("com.gorylenko.gradle-git-properties:gradle-git-properties:2.5.7")
    testImplementation("org.springframework.boot:spring-boot-gradle-plugin:4.0.5")
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

    java.sourceCompatibility = JavaVersion.VERSION_21
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
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
