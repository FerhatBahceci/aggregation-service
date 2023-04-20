package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import reactor.core.publisher.Flux;

public interface TrackGateway {
    Flux<TrackResponse> getTracking(String track);
}
