package com.valensas.observability.coroutine

import io.opentelemetry.api.trace.Span
import io.opentelemetry.sdk.trace.SdkTracerProvider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals

class CoroutineTracingTest {
    @Test
    fun `detached coroutine inherits current trace`() =
        runBlocking {
            val tracerProvider = SdkTracerProvider.builder().build()
            val span = tracerProvider.get("test").spanBuilder("parent").startSpan()
            val detachedScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val observedTraceId = CompletableDeferred<String>()

            try {
                span.makeCurrent().use {
                    detachedScope.launchWithCurrentTrace {
                        observedTraceId.complete(Span.current().spanContext.traceId)
                    }
                }

                assertEquals(span.spanContext.traceId, withTimeout(5_000) { observedTraceId.await() })
            } finally {
                detachedScope.cancel()
                span.end()
                tracerProvider.close()
            }
        }
}
