package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;
import java.util.Map;

public class Shipment extends Response<Long, List<String>> {
    @JsonCreator
    public Shipment(Map<Long, List<String>> shipments) {
        super(shipments);
    }

    @Override
    public String toString() {
        return "ShipmentResponse={shipments=" + getResponseMap() + "}";
    }
}
