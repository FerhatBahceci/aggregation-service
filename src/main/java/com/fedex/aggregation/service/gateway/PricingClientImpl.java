package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.Set;

@Component
public class PricingClientImpl implements PricingGateway {

    private final WebClient client;

    public PricingClientImpl(@Qualifier("pricingClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Flux<PricingResponse> getPricing(Set<String> countryCodes) {
        return !countryCodes.isEmpty()
                ? getPricing(String.join(",", countryCodes))
                : Flux.empty();
    }

    private Flux<PricingResponse> getPricing(String countryCodes) {
        return client
                .get()
                .uri(builder ->
                        builder.path("/pricing").queryParam("q", countryCodes).build()
                )
                .retrieve()
                .bodyToFlux(PricingResponse.class);
    }
}

