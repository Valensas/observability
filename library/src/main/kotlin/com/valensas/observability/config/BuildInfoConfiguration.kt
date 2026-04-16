
package com.valensas.observability.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.context.annotation.Configuration

@Configuration
open class BuildInfoConfiguration(
    private val registry: MeterRegistry,
    private val infoEndpoint: InfoEndpoint,
) {
    init {
        val labels = flatten(infoEndpoint.info()).map { (k, v) -> Tag.of(k, v) }
        labels.forEach {
            println("${it.key} -> ${it.value}")
        }
        registry.gauge("build_info", labels, 1)
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