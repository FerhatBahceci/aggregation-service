package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;

public class PricingResponse {
    private final Map<String, Double> pricing;

    @JsonCreator
    public PricingResponse(Map<String, Double> pricing) {
        this.pricing = pricing;
    }

    public Map<String, Double> getPricing() {
        return pricing;
    }

}