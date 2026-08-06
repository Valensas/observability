package com.valensas.observability.webflux

import io.opentelemetry.api.trace.Span
import org.springframework.core.Ordered
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/** Exposes the active OpenTelemetry trace ID as an HTTP response header. */
class TraceIdResponseWebFilter(
    private val headerName: String
) : WebFilter,
    Ordered {
    override fun getOrder(): Int = FILTER_ORDER

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain
    ): Mono<Void> =
        Mono.defer {
            val spanContext = Span.current().spanContext
            if (spanContext.isValid) {
                exchange.response.headers.set(headerName, spanContext.traceId)
            }
            chain.filter(exchange)
        }

    private companion object {
        // OpenTelemetry's WebFlux filter uses HIGHEST_PRECEDENCE + 1 in instrumentation 2.28.0.
        const val FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 2
    }
}
