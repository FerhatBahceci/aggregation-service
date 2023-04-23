package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.Pricing;
import reactor.core.publisher.Flux;

public interface PricingGateway {
    Flux<Pricing> getPricing(String countryCodes);
}
