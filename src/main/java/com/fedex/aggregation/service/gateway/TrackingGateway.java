package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import reactor.core.publisher.Mono;

public interface TrackingGateway {
    Mono<TrackResponse> getTracking(String track);
}
