package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import static com.fedex.aggregation.service.util.StringUtil.getStringSet;

@Component
public class ShipmentClientImpl extends BulkRequestHandler<ShipmentResponse> implements ShipmentGateway {
    private static final Logger logger = LoggerFactory.getLogger(ShipmentClientImpl.class);
    private final WebClient client;
    private final Sinks.Many<ShipmentResponse> shipmentSink;
    private final Flux<ShipmentResponse> flux;
    public static final ShipmentResponse defaultShipmentResponse = new ShipmentResponse(null);

    public ShipmentClientImpl(@Qualifier("shipmentClient") WebClient webClient,
                              @Autowired Sinks.Many<ShipmentResponse> shipmentSink,
                              @Autowired Flux<ShipmentResponse> flux) {
        this.client = webClient;
        this.shipmentSink = shipmentSink;
        this.flux = flux;
    }

    @Override
    public Flux<ShipmentResponse> getShipment(String orderIds) {
        getBulkCallsOrWait(this::get, getStringSet(orderIds), shipmentSink);
        return flux;
    }

    public Flux<ShipmentResponse> get(String orderIds) {
        logger.info("Calling Shipment API with following orderIds={}", orderIds);
        return (!orderIds.isBlank() ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/shipments").queryParam("q", orderIds).build()
                        )
                        .retrieve()
                        .bodyToFlux(ShipmentResponse.class)
                        .onErrorReturn(defaultShipmentResponse)
                : Flux.empty());
    }
}
