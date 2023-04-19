package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PricingClientImpl implements PricingGateway {
    private final WebClient client;
    private final OverLoadingPreventionHandler<PricingResponse> overloadingPreventionHandler = new OverLoadingPreventionHandler<>();

    public PricingClientImpl(@Qualifier("pricingClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Mono<PricingResponse> getPricing(String countryCodes) {
        return !countryCodes.isBlank()
                ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/pricing").queryParam("q", countryCodes).build()
                        )
                        .retrieve()
                        .bodyToMono(PricingResponse.class)
                : Mono.empty();
    }
}

