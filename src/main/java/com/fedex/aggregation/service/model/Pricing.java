package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;

public class Pricing extends Response<Pricing, String, Double> {

    @JsonCreator
    public Pricing(Map<String, Double> pricing) {
        super(pricing);
    }

    @Override
    public String toString() {
        return "PricingResponse={pricing=" + getResponseMap() + "}";
    }

}