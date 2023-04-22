package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrackGateway {
    Flux<List<TrackResponse>> getTracking(String track);
    Mono<TrackResponse> get(String orderIds);
}
