package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.Track;
import reactor.core.publisher.Flux;

public interface TrackGateway {
    Flux<Track> getTracking(String track);
}
