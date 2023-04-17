package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import reactor.core.publisher.Mono;
import java.util.List;

public interface TrackingGateway {
    Mono<TrackResponse> getTracking(List<Long> track);
}
