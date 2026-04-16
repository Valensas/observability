package com.valensas.observability.config

import io.micrometer.core.instrument.ImmutableTag
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(MeterRegistry::class)
@ConditionalOnProperty("valensas.observability.build-metrics.enabled", havingValue = "true", matchIfMissing = true)
class BuildInfoConfiguration(
    private val registry: MeterRegistry,
    private val infoEndpoint: InfoEndpoint,
    @Value("\${valensas.observability.build-metric.name:valensas_build_info}")
    private val name: String
) {
    init {
        val tags = flatten(infoEndpoint.info()).map { (k, v) -> ImmutableTag(k, v) }
        registry.gauge(name, tags, 1)
    }

    private fun flatten(map: Map<*, *>, prefix: String = ""): List<Pair<String, String>> =
        map.flatMap { (key, value) ->
            val fullKey = if (prefix.isEmpty()) "$key" else "$prefix.$key"
            when (value) {
                is Map<*, *> -> flatten(value, fullKey)
                else -> listOf(fullKey to value.toString())
            }
        }
}