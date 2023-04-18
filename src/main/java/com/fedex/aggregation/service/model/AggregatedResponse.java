package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

public record AggregatedResponse(
        @JsonInclude Map<String, Double> pricing,
        @JsonInclude Map<Long, TrackResponse.Status> track,
        @JsonInclude Map<Long, List<String>> shipments) {
}
