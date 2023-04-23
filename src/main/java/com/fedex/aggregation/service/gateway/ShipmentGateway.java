package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.Shipment;
import reactor.core.publisher.Flux;

public interface ShipmentGateway {
    Flux<Shipment> getShipment(String orderIds);
}
