package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.Pricing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class PricingClient extends OverloadingPreventionHandler implements PricingGateway {
    private static final Logger logger = LoggerFactory.getLogger(PricingClient.class);
    private final WebClient client;
    public static final Pricing DEFAULT_PRICING = new Pricing(null);

    public PricingClient(@Qualifier("pricingWebClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Flux<Pricing> getPricing(String countryCodes) {
        return get(countryCodes, this::get, Pricing::new)
                .doOnNext(pricingResponse -> logger.info("Fetched PricingResponse:{}", pricingResponse));
    }

    public Mono<Pricing> get(String countryCodes) {
        logger.info("Calling Pricing API with following countryCodes={}", countryCodes);
        return (!countryCodes.isEmpty()
                ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/pricing").queryParam("q", countryCodes).build()
                        )
                        .retrieve()
                        .bodyToMono(Pricing.class)
                        .onErrorReturn(new Pricing(Map.of()))
                : Mono.empty());
    }
}

