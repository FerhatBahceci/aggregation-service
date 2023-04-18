package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import reactor.core.publisher.Flux;
import java.util.Set;

public interface ShipmentGateway {
    Flux<ShipmentResponse> getShipment(Set<Long> orderIds);
}
