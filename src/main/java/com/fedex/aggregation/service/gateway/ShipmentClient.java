package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.Shipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;

@Component
public class ShipmentClient extends OverloadingPreventionHandler implements ShipmentGateway {
    private static final Logger logger = LoggerFactory.getLogger(ShipmentClient.class);
    private final WebClient client;
    public static final Shipment DEFAULT_SHIPMENT = new Shipment(null);

    public ShipmentClient(@Qualifier("shipmentWebClient") WebClient webClient) {
        this.client = webClient;
    }

    @Override
    public Flux<Shipment> getShipment(String orderIds) {
        return get(orderIds, this::get, Shipment::new)
                .doOnNext(shipmentResponse -> logger.info("Fetched ShipmentResponse:{}", shipmentResponse));
    }

    public Mono<Shipment> get(String orderIds) {                             // It is unclear from the task description if it should actually be 5x5 q=1,2,3,4,5   5x1 = or q1=1, q2=2 q3=3, q4=4, q5=5.
        logger.info("Calling Shipment API with following orderIds={}", orderIds);
        return (!orderIds.isEmpty() ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/shipments").queryParam("q", orderIds).build()
                        )
                        .retrieve()
                        .bodyToMono(Shipment.class)
                        .onErrorReturn(new Shipment(Map.of()))
                : Mono.empty());
    }
}
