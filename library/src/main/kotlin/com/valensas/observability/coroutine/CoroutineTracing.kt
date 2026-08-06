package com.valensas.observability.coroutine

import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches a coroutine with the OpenTelemetry context that is active at the call site.
 *
 * This is intended for detached scopes that do not inherit the request's coroutine context.
 */
fun CoroutineScope.launchWithCurrentTrace(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context + Context.current().asContextElement(), start, block)
