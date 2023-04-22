package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public String toString() {
        return "TrackResponse={track=" + track + "}";
    }

    public static Map<Long, Status> mergeTrack(List<TrackResponse> responseList) {
        return responseList.stream().map(TrackResponse::getTrack)
                .reduce((trackMap1, trackMap2) ->
                        Stream.concat(trackMap1.entrySet().stream(), trackMap2.entrySet().stream())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (track1, track2) -> track2))).orElse(Map.of());
    }
}
