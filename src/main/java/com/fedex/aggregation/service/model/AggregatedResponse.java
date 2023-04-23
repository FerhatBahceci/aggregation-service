package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;
import java.util.Map;

public class AggregatedResponse {
    private Map<String, Double> pricing;
    private Map<Long, Track.Status> track;

    private Map<Long, List<String>> shipments;

    @JsonCreator
    public AggregatedResponse() {
    }

    public AggregatedResponse setPricing(Map<String, Double> pricing) {
        this.pricing = pricing;
        return this;
    }

    public AggregatedResponse setTrack(Map<Long, Track.Status> track) {
        this.track = track;
        return this;
    }

    public AggregatedResponse setShipments(Map<Long, List<String>> shipments) {
        this.shipments = shipments;
        return this;
    }

    @Override
    public String toString() {
        return "AggregatedResponse={pricing=" + pricing + ", track=" + track + ", shipments=" + shipments + "}";
    }
}
