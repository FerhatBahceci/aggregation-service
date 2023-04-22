package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ShipmentGateway {
    Flux<List<ShipmentResponse>> getShipment(String orderIds);
    Mono<ShipmentResponse> get(String orderIds);
}
