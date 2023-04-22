package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Component
public class ShipmentClient implements ShipmentGateway {
    private static final Logger logger = LoggerFactory.getLogger(ShipmentClient.class);
    private final WebClient client;
    public static final List<ShipmentResponse> defaultShipmentResponse = List.of();

    public ShipmentClient(@Qualifier("shipmentWebClient") WebClient webClient) {
        this.client = webClient;
    }

    @Override
    public Flux<List<ShipmentResponse>> getShipment(String orderIds) {
        return Flux.empty();

        /*flux.doOnComplete(() -> {
                    logger.info("COMPLETED!");
                })
                .doOnNext(shipmentResponse -> {
*//*
                            callbackQueue.add(shipmentResponse);
*//*
                            logger.info("This is the subscribed ShipmentResponse:{}", shipmentResponse);
                        }
                );*/
    }

    public Mono<ShipmentResponse> get(String orderIds) {
        logger.info("Calling Shipment API with following orderIds={}", orderIds);
        return (!orderIds.isBlank() ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/shipments").queryParam("q", orderIds).build()
                        )
                        .retrieve()
                        .bodyToMono(ShipmentResponse.class)
                        .onErrorReturn(new ShipmentResponse(Map.of()))
/*
                        .log()
*/
                : Mono.empty());
    }
}
