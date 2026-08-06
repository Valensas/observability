# Valensas Observability

A simple library to manage observability needs for Spring Boot.

## Usage

### Installation

Include in your `build.gradle.kts`:

```kotlin

dependencies {
    implementation("com.valensas:observability:$observabilityVersion")
}

// Needed for dependency version metrics only
plugins {
    id("com.valensas.observability-artifacts") version "$observabilityVersion"
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.getByName("observabilityArtifacts"))
}
```

### WebClient metrics

This feature allows to expose [Micrometer](http://micrometer.io) metrics for [Spring's WebClient](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html).

```kotlin
val webClient = WebClient.builder().observationConvention(IntegrationRequestObservationConvention("metric.name"))
```

### Feign Micrometer Capability auto configuration

This feature is automatically enabled when [Feign](https://github.com/OpenFeign/feign) and [Micrometer](http://micrometer.io)
are configured. It enables Micrometer metrics for Feign requests.

### B3 Header propagation

Allows for customization of [B3 header propagation](https://github.com/openzipkin/b3-propagation). This configuration
is enabled when `management.tracing.propagation.type=B3` and the header format can be configured using
`management.tracing.propagation.format=SINGLE/MULTI/SINGLE_NO_PARENT`. The default value is `SINGLE`.

### Coroutine trace context

Detached coroutine scopes do not automatically inherit the trace context of the request that launches them. Use
`launchWithCurrentTrace` when detached work must remain correlated with the active OpenTelemetry trace:

```kotlin
import com.valensas.observability.coroutine.launchWithCurrentTrace

CoroutineScope(Dispatchers.IO).launchWithCurrentTrace {
    logger.info("Detached work started")
}
```

The helper remains non-blocking and returns a regular `Job`. Structured coroutine code that already inherits its
parent context does not need this helper.

### Trace ID response header

Reactive applications expose the active OpenTelemetry trace ID to API callers by default. The header name can be
customized or the feature can be disabled explicitly:

```yaml
valensas:
  observability:
    trace-response-header:
      enabled: false
      name: X-Trace-Id
```

The feature only exposes the current trace ID; service-to-service propagation continues to use the configured
OpenTelemetry propagator, such as the W3C `traceparent` header.

### Version metrics

This feature allows to expose you application's dependencies' versions to Micrometer. This feature
is enabled when Micrometer is configured and `valensas.observability.version-metrics.enabled=true` (default is true).
The metric name can be configured using the `valensas.observability.version-metric.name` property (default is `valensas_application`).

For this feature to work properly, you will need the use the `com.valensas.observability-artifacts` plugin as described
in the installation section.
