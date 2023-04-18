package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class ShipmentClientImpl implements ShipmentGateway {

    private final AtomicInteger requestCounter  = new AtomicInteger();

    private final WebClient client;

    public ShipmentClientImpl(@Qualifier("shipmentClient") WebClient webClient) {
        this.client = webClient;
    }

    @Override
    public Mono<ShipmentResponse> getShipment(Set<Long> orderIds) {
        return !orderIds.isEmpty()
                ? getShipment(orderIds.stream().map(Object::toString).collect(Collectors.joining(",")))
                : Mono.empty();
    }

    private Mono<ShipmentResponse> getShipment(String orderIds) {
        return client
                .get()
                .uri(builder ->
                        builder.path("/shipments").queryParam("q", orderIds).build()
                )
                .retrieve()
                .bodyToMono(ShipmentResponse.class);
    }
}
