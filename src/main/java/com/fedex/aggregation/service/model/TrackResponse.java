package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class TrackResponse {

    private final Map<Long, Status> track;

    @JsonCreator
    public TrackResponse(Map<Long, Status> track) {
        this.track = track;
    }

    public Map<Long, Status> getTrack() {
        return track;
    }

    public enum Status {
        NEW,
        @JsonProperty("IN TRANSIT") //Required due to space
        IN_TRANSIT,
        COLLECTING,
        COLLECTED,
        DELIVERING,
        DELIVERED;
    }
}
