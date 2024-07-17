package com.valensas.observability.client

import io.micrometer.common.KeyValue
import io.micrometer.common.KeyValues
import org.springframework.web.reactive.function.client.ClientRequestObservationContext
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention

class IntegrationRequestObservationConvention(
    name: String = "integration.request"
) : DefaultClientRequestObservationConvention(name) {
    override fun getLowCardinalityKeyValues(context: ClientRequestObservationContext): KeyValues =
        super.getLowCardinalityKeyValues(context).and(
            KeyValue.of(
                "clientName",
                context.carrier!!
                    .build()
                    .url()
                    .host
            )
        )
}
