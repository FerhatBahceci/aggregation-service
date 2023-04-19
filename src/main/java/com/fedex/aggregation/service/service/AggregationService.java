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
import reactor.core.publisher.Mono;

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
        var defaultPricingResponse = new PricingResponse(null);
        var defaultTrackResponse = new TrackResponse(null);
        var defaultShipmentResponse = new ShipmentResponse(null);

        final Mono<PricingResponse> pricingResponseMono =
                pricingGateway.getPricing(pricing)
/*
                        .delayUntil()
*/
                        .switchIfEmpty(Mono.just(defaultPricingResponse))
                        .onErrorReturn(defaultPricingResponse);

        final Mono<TrackResponse> trackResponseMono =
                trackGateway.getTracking(track)
/*
                        .delayUntil()
*/
                        .switchIfEmpty(Mono.just(defaultTrackResponse))
                        .onErrorReturn(defaultTrackResponse);

        final Mono<ShipmentResponse> shipmentResponseMono =
                shipmentGateway.getShipment(shipments)
/*
                        .delayUntil()
*/
                        .switchIfEmpty(Mono.just(defaultShipmentResponse))
                        .onErrorReturn(defaultShipmentResponse);

        return Mono.just(new AggregatedResponse())
                .zipWith(pricingResponseMono)
                .map(p -> p.getT1().setPricing(p.getT2().getPricing()))
                .zipWith(trackResponseMono)
                .map(t -> t.getT1().setTrack(t.getT2().getTrack()))
                .zipWith(shipmentResponseMono)
                .map(s -> s.getT1().setShipments(s.getT2().getShipments()));
    }
}
