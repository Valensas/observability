package com.valensas.observability.config

import feign.Capability
import feign.micrometer.MicrometerCapability
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(MeterRegistry::class)
@ConditionalOnClass(MicrometerCapability::class)
open class FeignMicrometerAutoConfiguration {
    @Bean
    open fun micrometerCapability(meterRegistry: MeterRegistry): Capability = MicrometerCapability(meterRegistry)
}
