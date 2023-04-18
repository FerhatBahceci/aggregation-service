package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import reactor.core.publisher.Flux;
import java.util.Set;

public interface PricingGateway {
    Flux<PricingResponse> getPricing(Set<String> countryCodes);
}
