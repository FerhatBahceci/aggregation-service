package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Track extends Response<Long, Track.Status> {

    @JsonCreator
    public Track(Map<Long, Status> track) {
        super(track);
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

    @Override
    public String toString() {
        return "TrackResponse={track=" + getResponseMap() + "}";
    }
}
