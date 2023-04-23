package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import reactor.core.publisher.Flux;

public interface PricingGateway {
    Flux<PricingResponse> getPricing(String countryCodes);
}
