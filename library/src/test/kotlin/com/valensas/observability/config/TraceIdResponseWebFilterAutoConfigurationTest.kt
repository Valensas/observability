package com.valensas.observability.config

import com.valensas.observability.webflux.TraceIdResponseWebFilter
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TraceIdResponseWebFilterAutoConfigurationTest {
    private val contextRunner =
        ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TraceIdResponseWebFilterAutoConfiguration::class.java))

    @Test
    fun `filter is registered by default`() {
        contextRunner.run { context ->
            assertEquals(1, context.getBeansOfType(TraceIdResponseWebFilter::class.java).size)
        }
    }

    @Test
    fun `filter is not registered when disabled`() {
        contextRunner
            .withPropertyValues("valensas.observability.trace-response-header.enabled=false")
            .run { context ->
                assertTrue(context.getBeansOfType(TraceIdResponseWebFilter::class.java).isEmpty())
            }
    }
}
