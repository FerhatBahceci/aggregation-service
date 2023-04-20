package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import reactor.core.publisher.Flux;

public interface ShipmentGateway {
    Flux<ShipmentResponse> getShipment(String orderIds);
}
