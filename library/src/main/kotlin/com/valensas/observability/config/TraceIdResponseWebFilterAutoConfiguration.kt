package com.valensas.observability.config

import com.valensas.observability.webflux.TraceIdResponseWebFilter
import io.opentelemetry.api.trace.Span
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.server.WebFilter

@AutoConfiguration
@ConditionalOnClass(WebFilter::class, Span::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(
    prefix = "valensas.observability.trace-response-header",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(TraceResponseHeaderProperties::class)
open class TraceIdResponseWebFilterAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(TraceIdResponseWebFilter::class)
    open fun traceIdResponseWebFilter(properties: TraceResponseHeaderProperties): TraceIdResponseWebFilter =
        TraceIdResponseWebFilter(properties.name)
}

@ConfigurationProperties("valensas.observability.trace-response-header")
data class TraceResponseHeaderProperties(
    var enabled: Boolean = true,
    var name: String = "X-Trace-Id"
)
