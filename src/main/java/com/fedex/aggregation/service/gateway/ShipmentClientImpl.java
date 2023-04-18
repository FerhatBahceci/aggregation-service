package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShipmentClientImpl implements ShipmentGateway {

    private final WebClient client;

    public ShipmentClientImpl(@Qualifier("shipmentClient") WebClient webClient) {
        this.client = webClient;
    }

    @Override
    public Flux<ShipmentResponse> getShipment(Set<Long> orderIds) {
        return !orderIds.isEmpty()
                ? getShipment(orderIds.stream().map(Object::toString).collect(Collectors.joining(",")))
                : Flux.empty();
    }

    private Flux<ShipmentResponse> getShipment(String orderIds) {
        return client
                .get()
                .uri(builder ->
                        builder.path("/shipments").queryParam("q", orderIds).build()
                )
                .retrieve()
                .bodyToFlux(ShipmentResponse.class);
    }
}
