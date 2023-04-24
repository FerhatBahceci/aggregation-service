package com.fedex.aggregation.service.model;

import java.util.List;
import java.util.Map;

public class AggregatedResponse {
    private Map<String, Double> pricing;
    private Map<Long, Track.Status> track;

    private Map<Long, List<String>> shipments;


    public AggregatedResponse(Map<String, Double> pricing, Map<Long, Track.Status> track, Map<Long, List<String>> shipments) {
        this.pricing = pricing;
        this.track = track;
        this.shipments = shipments;
    }

    public AggregatedResponse() {
    }

    public Map<String, Double> getPricing() {
        return pricing;
    }

    public Map<Long, Track.Status> getTrack() {
        return track;
    }

    public Map<Long, List<String>> getShipments() {
        return shipments;
    }

    @Override
    public String toString() {
        return "AggregatedResponse={pricing=" + pricing + ", track=" + track + ", shipments=" + shipments + "}";
    }

    public void setPricing(Map<String, Double> pricing) {
        this.pricing = pricing;
    }

    public void setTrack(Map<Long, Track.Status> track) {
        this.track = track;
    }

    public void setShipments(Map<Long, List<String>> shipments) {
        this.shipments = shipments;
    }
}
