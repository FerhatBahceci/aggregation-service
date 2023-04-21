package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.fedex.aggregation.service.util.StringUtil.getStringSet;

@Component
public class ShipmentClient extends BulkRequestHandler<ShipmentResponse> implements ShipmentGateway {
    private static final Logger logger = LoggerFactory.getLogger(ShipmentClient.class);
    private final WebClient client;

    private final Flux<ShipmentResponse> flux;
    public static final ShipmentResponse defaultShipmentResponse = new ShipmentResponse(null);

    public ShipmentClient(@Qualifier("shipmentWebClient") WebClient webClient,
                          @Autowired Sinks.Many<ShipmentResponse> shipmentSink,
                          @Autowired Flux<ShipmentResponse> flux) {
        super(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), shipmentSink);
        this.client = webClient;
        this.flux = flux;
    }

    @Override
    public Flux<ShipmentResponse> getShipment(String orderIds) {
        getBulkCallsOrWait(this::get, getStringSet(orderIds));
        return flux.doOnComplete(() -> {
                    logger.info("COMPLETED!");
                    getSink().emitComplete((signalType, emitResult) -> emitResult.isSuccess());
                })
                .doOnNext(shipmentResponse ->
                        logger.info("This is the subscribed ShipmentResponse:{}", shipmentResponse)
                );
    }

    @Override
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
                        .onErrorReturn(defaultShipmentResponse)
/*
                        .log()
*/
                : Mono.empty());
    }
}
