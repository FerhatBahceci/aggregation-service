package com.fedex.aggregation.service.service;

/*This could possibly be a GraphQL endpoint that puzzles all the calls together by implementing datasources resolvers and schemas.*/

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackingGateway;
import com.fedex.aggregation.service.model.AggregatedResponse;
import com.fedex.aggregation.service.model.PricingResponse;
import com.fedex.aggregation.service.model.ShipmentResponse;
import com.fedex.aggregation.service.model.TrackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

import static java.util.Objects.nonNull;

@Service
public class AggregationService {

    private final PricingGateway pricingGateway;
    private final ShipmentGateway shipmentGateway;
    private final TrackingGateway trackingGateway;

    public AggregationService(@Autowired PricingGateway pricingClient,
                              @Autowired ShipmentGateway shipmentClient,
                              @Autowired TrackingGateway trackingClient) {
        this.pricingGateway = pricingClient;
        this.shipmentGateway = shipmentClient;
        this.trackingGateway = trackingClient;
    }

    public Mono<AggregatedResponse> getAggregation(
            Set<String> pricing,
            Set<Long> track,
            Set<Long> shipments) {

        var defaultPricingResponse = new PricingResponse(null);
        var defaultTrackResponse = new TrackResponse(null);
        var defaultShipmentResponse = new ShipmentResponse(null);
        return pricingGateway.getPricing(pricing)
                .switchIfEmpty(Mono.just(defaultPricingResponse))
                .onErrorReturn(defaultPricingResponse)  // For cases where the backing api fails to return a good result, due to either error or timeout, the field will still be included in the returned object, but the value will be ‘null’.
                .flatMap(pricingResponse -> trackingGateway.getTracking(track)
                        .switchIfEmpty(Mono.just(defaultTrackResponse))
                        .onErrorReturn(defaultTrackResponse)
                        .flatMap(trackResponse -> shipmentGateway.getShipment(shipments)
                                .switchIfEmpty(Mono.just(defaultShipmentResponse))
                                .onErrorReturn(defaultShipmentResponse)
                                .map(shipmentResponse ->
                                        new AggregatedResponse(
                                                nonNull(pricingResponse) ? pricingResponse.getPricing() : null,
                                                nonNull(trackResponse) ? trackResponse.getTrack() : null,
                                                nonNull(shipmentResponse) ? shipmentResponse.getShipments() : null
                                        )
                                )
                        )
                );
    }
}
