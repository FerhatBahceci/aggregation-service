package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import reactor.core.publisher.Flux;
import java.util.Set;

public interface TrackingGateway {
    Flux<TrackResponse> getTracking(Set<Long> track);
}
