package com.valensas.observability.webflux

import io.opentelemetry.sdk.trace.SdkTracerProvider
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import reactor.core.publisher.Mono
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TraceIdResponseWebFilterTest {
    private val filter = TraceIdResponseWebFilter("X-Trace-Id")

    @Test
    fun `adds active trace id to response`() {
        val tracerProvider = SdkTracerProvider.builder().build()
        val span = tracerProvider.get("test").spanBuilder("request").startSpan()
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test"))

        try {
            span.makeCurrent().use {
                filter.filter(exchange) { Mono.empty() }.block()
            }

            assertEquals(span.spanContext.traceId, exchange.response.headers.getFirst("X-Trace-Id"))
        } finally {
            span.end()
            tracerProvider.close()
        }
    }

    @Test
    fun `does not add header without an active trace`() {
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test"))

        filter.filter(exchange) { Mono.empty() }.block()

        assertNull(exchange.response.headers.getFirst("X-Trace-Id"))
    }
}
