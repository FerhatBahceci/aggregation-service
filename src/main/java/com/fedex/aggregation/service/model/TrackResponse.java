package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;

public class TrackResponse {

    private final Map<Long, Status> track;

    @JsonCreator
    public TrackResponse(Map<Long, Status> track){
        this.track = track;
    }

    public Map<Long, Status> getTrack() {
        return track;
    }

    public enum Status {
        NEW,
        IN,
        TRANSIT,
        COLLECTING,
        COLLECTED,
        DELIVERING,
        DELIVERED;
    }
}
