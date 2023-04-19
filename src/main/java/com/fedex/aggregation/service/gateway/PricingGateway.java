package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import reactor.core.publisher.Mono;

public interface PricingGateway {
    Mono<PricingResponse> getPricing(String countryCodes);
}
