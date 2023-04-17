package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Set;

public interface TrackingGateway {
    Mono<TrackResponse> getTracking(Set<Long> track);
}
