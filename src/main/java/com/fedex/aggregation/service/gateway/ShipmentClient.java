package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;


@Component
public class ShipmentClient extends QueryParamsCreator implements ShipmentGateway {
    private static final Logger logger = LoggerFactory.getLogger(ShipmentClient.class);
    private final WebClient client;
    public static final List<ShipmentResponse> defaultShipmentResponse = List.of();

    public ShipmentClient(@Qualifier("shipmentWebClient") WebClient webClient) {
        this.client = webClient;
    }

    @Override
    public Flux<List<ShipmentResponse>> getShipment(String orderIds) {

        var executables = super.getExecutableRequests(orderIds);

        if (!executables.isEmpty()) {

            return Flux.just(executables.toArray(new String[executables.size()]))
                    .windowTimeout(1, Duration.ofSeconds(5))                // 1 single request contains q=1,2,3,4,5. The window in question buffers max 5 requests up to 5s from that the window was opened
                    .flatMap(stringFlux -> stringFlux.flatMap(this::get).collectList());
        } else {
            return Flux.just(executables.toArray(new String[executables.size()]))
                    .windowTimeout(1, Duration.ofSeconds(5))
                  /*  .delayUntil() */   // DelayUntil predicate of checking !executables.isEmpty() .delayUntil(-> predicate is matched)
                    .flatMap(stringFlux -> stringFlux.flatMap(this::get).collectList());
        }
    }


    public Mono<ShipmentResponse> get(String orderIds) {                             // It is unclear from the task description if it should actually be 5x5 q=1,2,3,4,5   5x1 = or q1=1, q2=2 q3=3, q4=4, q5=5.
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
