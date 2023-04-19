package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Set;

@Component
public class PricingClientImpl implements PricingGateway {
    private final WebClient client;

    public PricingClientImpl(@Qualifier("pricingClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Mono<PricingResponse> getPricing(Set<String> countryCodes) {
        return !countryCodes.isEmpty()
                ? getPricing(String.join(",", countryCodes))
                : Mono.empty();
    }

    private Mono<PricingResponse> getPricing(String countryCodes) {
        return client
                .get()
                .uri(builder ->
                        builder.path("/pricing").queryParam("q", countryCodes).build()
                )
                .retrieve()
                .bodyToMono(PricingResponse.class);
    }
}

