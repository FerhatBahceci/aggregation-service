package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.ShipmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import static com.fedex.aggregation.service.util.StringUtil.getSet;

@Component
public class ShipmentClientImpl extends BulkRequestHandler<ShipmentResponse> implements ShipmentGateway {
    private final WebClient client;
    private final Sinks.Many<ShipmentResponse> shipmentSink;
    private final Flux<ShipmentResponse> flux;

    public ShipmentClientImpl(@Qualifier("shipmentClient") WebClient webClient,
                              @Autowired Sinks.Many<ShipmentResponse> shipmentSink,
                              @Autowired Flux<ShipmentResponse> flux) {
        this.client = webClient;
        this.shipmentSink = shipmentSink;
        this.flux = flux;
    }

    @Override
    public Mono<ShipmentResponse> getShipment(String orderIds) {
        getBulkCallsOrWait(this::get, getSet(orderIds), shipmentSink);
        return Mono.from(flux);
    }

    public Mono<ShipmentResponse> get(String orderIds) {
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
