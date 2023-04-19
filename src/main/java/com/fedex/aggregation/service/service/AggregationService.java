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

import static java.util.Objects.nonNull;

@Service
public class AggregationService {
    private final PricingGateway pricingGateway;
    private final ShipmentGateway shipmentGateway;
    private final TrackGateway trackGateway;
    private static final PricingResponse defaultPricingResponse = new PricingResponse(null);
    private static final TrackResponse defaultTrackResponse = new TrackResponse(null);
    private static final ShipmentResponse defaultShipmentResponse = new ShipmentResponse(null);
    public static AggregatedResponse defaultAggregatedResponse = new AggregatedResponse();

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

        if (nonNull(pricing) || nonNull(track) || nonNull(shipments)) {
            final Mono<PricingResponse> pricingResponseMono = nonNull(pricing) ?
                    pricingGateway.getPricing(pricing)
                            .switchIfEmpty(Mono.just(defaultPricingResponse))
                            .onErrorReturn(defaultPricingResponse)
                    : Mono.just(defaultPricingResponse);

            final Mono<TrackResponse> trackResponseMono = nonNull(track) ?
                    trackGateway.getTracking(track)
                            .switchIfEmpty(Mono.just(defaultTrackResponse))
                            .onErrorReturn(defaultTrackResponse)
                    : Mono.just(defaultTrackResponse);

            final Mono<ShipmentResponse> shipmentResponseMono = nonNull(shipments) ?
                    shipmentGateway.getShipment(shipments)
                            .switchIfEmpty(Mono.just(defaultShipmentResponse))
                            .onErrorReturn(defaultShipmentResponse)
                    : Mono.just(defaultShipmentResponse);

            return Mono.just(new AggregatedResponse())
                    .zipWith(pricingResponseMono)
                    .map(p -> p.getT1().setPricing(p.getT2().getPricing()))
                    .zipWith(trackResponseMono)
                    .map(t -> t.getT1().setTrack(t.getT2().getTrack()))
                    .zipWith(shipmentResponseMono)
                    .map(s -> s.getT1().setShipments(s.getT2().getShipments()));
        } else {
            return Mono.just(defaultAggregatedResponse);
        }
    }
}
