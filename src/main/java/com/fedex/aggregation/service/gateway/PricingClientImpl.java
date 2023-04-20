package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static com.fedex.aggregation.service.util.StringUtil.getStringSet;

@Component
public class PricingClientImpl extends BulkRequestHandler<PricingResponse> implements PricingGateway {
    private static final Logger logger = LoggerFactory.getLogger(PricingClientImpl.class);
    private final WebClient client;
    private final Sinks.Many<PricingResponse> pricingSink;
    private final Flux<PricingResponse> flux;
    public static final PricingResponse defaultPricingResponse = new PricingResponse(null);

    public PricingClientImpl(@Qualifier("pricingClient") WebClient client,
                             @Autowired Sinks.Many<PricingResponse> pricingSink,
                             @Autowired Flux<PricingResponse> flux) {
        this.client = client;
        this.pricingSink = pricingSink;
        this.flux = flux;
    }

    @Override
    public Flux<PricingResponse> getPricing(String countryCodes) {
        getBulkCallsOrWait(this::get, getStringSet(countryCodes), pricingSink);
        return flux;
    }

    private Mono<PricingResponse> get(String countryCodes) {
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
                        .onErrorReturn(defaultPricingResponse)
                        .log()
                : Mono.empty());
    }
}

