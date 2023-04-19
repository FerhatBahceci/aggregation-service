package com.fedex.aggregation.service.service;

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

        final Mono<PricingResponse> pricingResponseMono =
                pricingGateway.getPricing(pricing)
                        .switchIfEmpty(Mono.just(defaultPricingResponse))
                        .onErrorReturn(defaultPricingResponse);

        final Mono<TrackResponse> trackResponseMono =
                trackingGateway.getTracking(track)
                        .switchIfEmpty(Mono.just(defaultTrackResponse))
                        .onErrorReturn(defaultTrackResponse);

        final Mono<ShipmentResponse> shipmentResponseMono =
                shipmentGateway.getShipment(shipments)
                        .switchIfEmpty(Mono.just(defaultShipmentResponse))
                        .onErrorReturn(defaultShipmentResponse);

        return Mono.just(new AggregatedResponse())
                .zipWith(pricingResponseMono)
                .map(setPricing -> setPricing.getT1().setPricing(setPricing.getT2().getPricing()))
                .zipWith(trackResponseMono)
                .map(setTrack -> setTrack.getT1().setTrack(setTrack.getT2().getTrack()))
                .zipWith(shipmentResponseMono)
                .map(setShipment -> setShipment.getT1().setShipments(setShipment.getT2().getShipments()));
    }
}
