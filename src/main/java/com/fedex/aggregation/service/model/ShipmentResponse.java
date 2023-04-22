package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShipmentResponse {
    private final Map<Long, List<String>> shipments;

    @JsonCreator
    public ShipmentResponse(Map<Long, List<String>> shipments) {
        this.shipments = shipments;
    }

    public Map<Long, List<String>> getShipments() {
        return shipments;
    }

    @Override
    public String toString() {
        return "ShipmentResponse={shipments=" + shipments + "}";
    }

    public static Map<Long, List<String>> mergeShipments(List<ShipmentResponse> responseList) {
        return responseList.stream().map(ShipmentResponse::getShipments)
                .reduce((shipmentMap1, shipmentMap2) ->
                        Stream.concat(shipmentMap1.entrySet().stream(), shipmentMap2.entrySet().stream())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))).orElse(Map.of());
    }
}
