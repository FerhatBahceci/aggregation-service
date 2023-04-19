package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import reactor.core.publisher.Mono;

public interface ShipmentGateway {
    Mono<ShipmentResponse> getShipment(String orderIds);
}
