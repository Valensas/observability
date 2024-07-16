package com.valensas.observability.config

import io.micrometer.core.instrument.ImmutableTag
import io.micrometer.core.instrument.MeterRegistry
import org.graalvm.home.Version
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootVersion
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream
import java.util.Properties

@Configuration
@ConditionalOnBean(MeterRegistry::class)
@ConditionalOnProperty("valensas.observability.version-metrics.enabled", havingValue = "true", matchIfMissing = true)
open class VersionMetricsAutoConfiguration(
    private val registry: MeterRegistry,
    @Value("\${valensas.observability.version-metric.name:valensas_application}")
    private val name: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        registerVersion("springboot", SpringBootVersion.getVersion() ?: "Unknown")
        registerVersion("java", Runtime.version().version().joinToString("."))

        if (System.getProperty("org.graalvm.home") != null) {
            registerVersion("graalvm", Version.getCurrent().toString())
        }
        registerPropertyFile("dependency-versions.properties")
        registerPropertyFile("project-info.properties")
    }

    private fun registerPropertyFile(filename: String) {
        val propertyFile = javaClass.classLoader.getResource(filename)?.file
        if (propertyFile == null) {
            logger.warn("{} does not exist", filename)
            return
        }

        val prop = Properties()
        FileInputStream(propertyFile).use { prop.load(it) }
        prop
            .stringPropertyNames()
            .forEach { registerVersion(it, prop.getProperty(it)) }
    }

    private fun registerVersion(
        component: String,
        version: String
    ) {
        registry.gauge(
            name,
            listOf(
                ImmutableTag("component", component),
                ImmutableTag("version", version)
            ),
            1
        )
    }
}
