package com.valensas.observability.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty("valensas.observability.build-metrics.enabled", havingValue = "true", matchIfMissing = true)
open class BuildInfoAutoConfiguration(
    private val registry: MeterRegistry,
    private val infoEndpoint: InfoEndpoint,
    @Value("\${valensas.observability.build-metrics.name:build_info}")
    private val name: String
) {
    init {
        val labels = flatten(infoEndpoint.info()).map { (k, v) -> Tag.of(k, v) }
        registry.gauge(name, labels, 1)
    }

    private fun flatten(
        map: Map<*, *>,
        prefix: String = ""
    ): List<Pair<String, String>> =
        map.flatMap { (key, value) ->
            val fullKey = if (prefix.isEmpty()) "$key" else "$prefix.$key"
            when (value) {
                is Map<*, *> -> flatten(value, fullKey)
                else -> listOf(fullKey to value.toString())
            }
        }
}
