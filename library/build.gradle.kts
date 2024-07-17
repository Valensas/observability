plugins {
    id("org.springframework.boot") version "3.3.1" apply false
    id("io.spring.dependency-management") version "1.1.6"
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation("org.graalvm.polyglot:polyglot:24.0.2")
    compileOnly("io.micrometer:micrometer-tracing-bridge-brave")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator")
    compileOnly("io.github.openfeign:feign-micrometer:13.3")
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
}


publishing {
    publications {
        create("library", MavenPublication::class.java) {
            artifactId = "observability"
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
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