package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ShipmentGateway {
    Mono<ShipmentResponse> getShipment(List<Long> orderIds);
}
