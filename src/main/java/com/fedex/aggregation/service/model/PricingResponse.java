package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PricingResponse {
    private final Map<String, Double> pricing;

    @JsonCreator
    public PricingResponse(Map<String, Double> pricing) {
        this.pricing = pricing;
    }

    public Map<String, Double> getPricing() {
        return pricing;
    }

    @Override
    public String toString() {
        return "PricingResponse={pricing=" + pricing + "}";
    }

    public static Map<String, Double> mergePricing(List<PricingResponse> responseList) {
        return responseList.stream().map(PricingResponse::getPricing)
                .reduce((pricingMap1, pricingMap2) ->
                        Stream.concat(pricingMap1.entrySet().stream(), pricingMap2.entrySet().stream())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (pricing1, pricing2) -> pricing1 > pricing2 ? pricing1 : pricing2)))
                .orElse(Map.of());
    }
}