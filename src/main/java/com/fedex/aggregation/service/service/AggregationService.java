package com.fedex.aggregation.service.service;

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackGateway;
import com.fedex.aggregation.service.model.AggregatedResponse;
import com.fedex.aggregation.service.model.PricingResponse;
import com.fedex.aggregation.service.model.ShipmentResponse;
import com.fedex.aggregation.service.model.TrackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.fedex.aggregation.service.gateway.PricingClient.defaultPricingResponse;
import static com.fedex.aggregation.service.gateway.ShipmentClient.defaultShipmentResponse;
import static com.fedex.aggregation.service.gateway.TrackClient.defaultTrackResponse;
import static java.util.Objects.nonNull;

@Service
public class AggregationService {
    private final PricingGateway pricingGateway;
    private final ShipmentGateway shipmentGateway;
    private final TrackGateway trackGateway;

    public AggregationService(@Autowired PricingGateway pricingClient,
                              @Autowired ShipmentGateway shipmentClient,
                              @Autowired TrackGateway trackingClient) {
        this.pricingGateway = pricingClient;
        this.shipmentGateway = shipmentClient;
        this.trackGateway = trackingClient;
    }

    public Mono<AggregatedResponse> getAggregation(
            String pricing,
            String track,
            String shipments) {

        final Flux<PricingResponse> pricingResponseFlux = nonNull(pricing) ?
                pricingGateway.getPricing(pricing)
                        .onErrorReturn(defaultPricingResponse)
                : Flux.empty();

        final Flux<TrackResponse> trackResponseFlux = nonNull(track) ?
                trackGateway.getTracking(track)
                        .onErrorReturn(defaultTrackResponse)
                : Flux.empty();

        final Flux<ShipmentResponse> shipmentResponseFlux = nonNull(shipments) ?
                shipmentGateway.getShipment(shipments)
                        .onErrorReturn(defaultShipmentResponse)
                : Flux.empty();

        return Mono.from(Flux.zip(pricingResponseFlux, shipmentResponseFlux, trackResponseFlux)
                .mapNotNull(r -> {
                    var agg = new AggregatedResponse();
                    agg.setPricing(r.getT1().getPricing());
                    agg.setShipments(r.getT2().getShipments());
                    agg.setTrack(r.getT3().getTrack());
                    return agg;
                }));
    }
}