package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static com.fedex.aggregation.service.util.StringUtil.getSet;

@Component
public class PricingClientImpl extends BulkRequestHandler<PricingResponse> implements PricingGateway {
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
    public Mono<PricingResponse> getPricing(String countryCodes) {
        getBulkCallsOrWait(this::get, getSet(countryCodes), pricingSink);
        return Mono.from(flux);
    }

    private Mono<PricingResponse> get(String countryCodes) {
        return !countryCodes.isBlank()
                ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/pricing").queryParam("q", countryCodes).build()
                        )
                        .retrieve()
                        .bodyToMono(PricingResponse.class)
                        .onErrorReturn(defaultPricingResponse)
                : Mono.empty();
    }
}

