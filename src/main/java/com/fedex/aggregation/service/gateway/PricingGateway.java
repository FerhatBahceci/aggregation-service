package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import reactor.core.publisher.Mono;
import java.util.Set;

public interface PricingGateway {
    Mono<PricingResponse> getPricing(Set<String> countryCodes);
}
