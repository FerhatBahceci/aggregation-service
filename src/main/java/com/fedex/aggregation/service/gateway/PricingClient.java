package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class PricingClient implements PricingGateway {
    private static final Logger logger = LoggerFactory.getLogger(PricingClient.class);
    private final WebClient client;
    public static final List<PricingResponse> defaultPricingResponse = List.of();

    public PricingClient(@Qualifier("pricingWebClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Flux<List<PricingResponse>> getPricing(String countryCodes) {
        return Flux.empty();

        /*flux.doOnComplete(() -> {
                    logger.info("COMPLETED!");
                })
                .doOnNext(pricingResponse -> {
*//*
                            callbackQueue.add(pricingResponse);
*//*
                            logger.info("This is the subscribed PricingResponse:{}", pricingResponse);
                        }
                );*/
    }

    @Override
    public Mono<PricingResponse> get(String countryCodes) {
        logger.info("Calling Pricing API with following countryCodes={}", countryCodes);
        return (!countryCodes.isBlank()
                ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/pricing").queryParam("q", countryCodes).build()
                        )
                        .retrieve()
                        .bodyToMono(PricingResponse.class)
                        .onErrorReturn(new PricingResponse(Map.of()))
/*
                        .log()
*/
                : Mono.empty());
    }
}

