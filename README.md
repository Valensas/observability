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

### Version metrics

This feature allows to expose you application's dependencies' versions to Micrometer. This feature
is enabled when Micrometer is configured and `valensas.observability.version-metrics.enabled=true` (default is true).
The metric name can be configured using the `valensas.observability.version-metric.name` property (default is `valensas_application`).

For this feature to work properly, you will need the use the `com.valensas.observability-artifacts` plugin as described
in the installation section.
