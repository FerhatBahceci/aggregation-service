package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class PricingClient extends QueryParamsCreator implements PricingGateway {
    private static final Logger logger = LoggerFactory.getLogger(PricingClient.class);
    private final WebClient client;
    public static final List<PricingResponse> defaultPricingResponse = List.of();

    public PricingClient(@Qualifier("pricingWebClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Flux<List<PricingResponse>> getPricing(String countryCodes) {
        var executables = super.getExecutableRequests(countryCodes);
        return Flux.just(executables.toArray(new String[executables.size()]))
                .windowTimeout(5, Duration.ofSeconds(5))                // 1 single request contains q=1,2,3,4,5. The window need to contain 5xq before firing of the calls, The window in question buffers max 5 requests up to 5s from that the window was opened
                .flatMap(stringFlux -> stringFlux.flatMap(this::get).collectList())
                .doOnNext((pricingResponses) -> logger.info("Fetched PricingResponses:{}", pricingResponses));  //TODO Ensure that we are suspending this call until !queryParams.isEmpty()
    }

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

