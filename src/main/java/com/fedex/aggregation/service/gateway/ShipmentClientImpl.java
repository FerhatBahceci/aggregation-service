package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Component
public class ShipmentClientImpl implements ShipmentGateway {

    private final WebClient client;

    public ShipmentClientImpl(@Qualifier("shipmentClient") WebClient webClient) {
        this.client = webClient;
    }

    @Override
    public Mono<ShipmentResponse> getShipment(String orderIds) {
        return !orderIds.isBlank() ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/shipments").queryParam("q", orderIds).build()
                        )
                        .retrieve()
                        .bodyToMono(ShipmentResponse.class)

                : Mono.empty();
    }
}
