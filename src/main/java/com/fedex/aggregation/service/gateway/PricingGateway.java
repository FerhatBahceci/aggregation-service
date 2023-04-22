package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import reactor.core.publisher.Flux;
import java.util.List;

public interface PricingGateway {
    Flux<List<PricingResponse>> getPricing(String countryCodes);
}
