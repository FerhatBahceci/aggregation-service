package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PricingGateway {
    Flux<PricingResponse> getPricing(String countryCodes);
    Mono<PricingResponse> get(String countryCodes);
}
