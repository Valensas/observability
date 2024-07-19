package com.valensas.observability.config

import brave.baggage.BaggagePropagation
import brave.baggage.BaggagePropagationCustomizer
import brave.propagation.B3Propagation
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@AutoConfigureBefore(BraveAutoConfiguration::class)
@ConditionalOnProperty(value = ["management.tracing.propagation.type"], havingValue = "B3", matchIfMissing = false)
open class TracingAutoConfiguration {
    @Bean
    open fun propagationFactoryBuilder(
        @Value("\${management.tracing.propagation.format:SINGLE}")
        format: B3Propagation.Format,
        baggagePropagationCustomizers: ObjectProvider<BaggagePropagationCustomizer>
    ): BaggagePropagation.FactoryBuilder {
        val delegate = B3Propagation.newFactoryBuilder().injectFormat(format).build()
        val builder = BaggagePropagation.newFactoryBuilder(delegate)
        baggagePropagationCustomizers.orderedStream().forEach { customizer: BaggagePropagationCustomizer -> customizer.customize(builder) }
        return builder
    }
}
