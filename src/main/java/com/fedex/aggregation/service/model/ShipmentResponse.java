package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import java.util.Map;

public class ShipmentResponse {
    private final Map<Long, List<String>> shipments;

    @JsonCreator
    public ShipmentResponse(Map<Long, List<String>> shipments) {
        this.shipments = shipments;
    }

    public Map<Long, List<String>> getShipments() {
        return shipments;
    }
}
