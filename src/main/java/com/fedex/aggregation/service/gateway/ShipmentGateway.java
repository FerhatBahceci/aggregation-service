package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface ShipmentGateway {
    Mono<ShipmentResponse> getShipment(Set<Long> orderIds);
}
