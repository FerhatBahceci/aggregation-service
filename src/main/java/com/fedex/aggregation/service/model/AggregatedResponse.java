package com.fedex.aggregation.service.model;

import java.util.List;
import java.util.Map;

public class AggregatedResponse {
    private Map<String, Double> pricing;
    private Map<Long, TrackResponse.Status> track;

    public AggregatedResponse() {
    }

    private Map<Long, List<String>> shipments;

    public Map<String, Double> getPricing() {
        return pricing;
    }

    public Map<Long, TrackResponse.Status> getTrack() {
        return track;
    }

    public Map<Long, List<String>> getShipments() {
        return shipments;
    }

    public AggregatedResponse setPricing(Map<String, Double> pricing) {
        this.pricing = pricing;
        return this;
    }

    public AggregatedResponse setTrack(Map<Long, TrackResponse.Status> track) {
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
