import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

plugins {
    `java-gradle-plugin`
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.springframework.boot") version "3.3.1" apply false
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jmailen.kotlinter") version "4.4.0"
    id("maven-publish")
    id("java-library")
    kotlin("jvm") version "1.9.21"
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
}

group = "com.valensas"
version = "3.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

gradlePlugin {
    val observabilityArtifactsPlugin by plugins.creating {
        id = "com.valensas.observability-artifacts"
        implementationClass = "com.valensas.observability.plugin.ObservabilityArtifactsPlugin"
    }
}

dependencies {
    implementation("org.graalvm.polyglot:polyglot:24.0.1")
    compileOnly("io.micrometer:micrometer-tracing-bridge-brave")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator")
    compileOnly("io.github.openfeign:feign-micrometer:13.3")
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            groupId = "com.valensas.observability-artifacts"
            artifactId = "com.valensas.observability-artifacts"
            from(components["java"])
        }
        create<MavenPublication>("artifact") {
            groupId = "com.valensas"
            artifactId = "observability"
            from(components["java"])
        }
    }
}

tasks.getByName<Jar>("jar") {
    archiveClassifier = ""
}


signing {
    val keyId = System.getenv("SIGNING_KEYID")
    val secretKey = System.getenv("SIGNING_SECRETKEY")
    val passphrase = System.getenv("SIGNING_PASSPHRASE")

    useInMemoryPgpKeys(keyId, secretKey, passphrase)
}

centralPortal {
    username = System.getenv("SONATYPE_USERNAME")
    password = System.getenv("SONATYPE_PASSWORD")

    pom {
        name = "Observability"
        description = "A simple library to manage observability needs for Spring Boot."
        url = "https://valensas.com/"
        scm {
            url = "https://github.com/Valensas/observability"
        }

        licenses {
            license {
                name.set("MIT License")
                url.set("https://mit-license.org")
            }
        }

        developers {
            developer {
                id.set("0")
                name.set("Valensas")
                email.set("info@valensas.com")
            }
        }
    }
}