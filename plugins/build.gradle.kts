plugins {
    `java-gradle-plugin`
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
}

gradlePlugin {
    plugins {
        create("artifacts") {
            id = "com.valensas.observability-artifacts"
            implementationClass = "com.valensas.observability.plugin.ObservabilityArtifactsPlugin"
        }
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
        name = "Observability Plugins"
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