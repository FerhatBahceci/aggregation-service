package com.fedex.aggregation.service.model;

import java.util.List;
import java.util.Map;
public record AggregatedResponse(
        Map<String, Double> pricing,
        Map<Long, TrackResponse.Status> track,
        Map<Long, List<String>> shipments) {
}
