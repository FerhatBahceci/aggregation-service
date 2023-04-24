package com.fedex.aggregation.service.service;

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackGateway;
import com.fedex.aggregation.service.model.AggregatedResponse;
import com.fedex.aggregation.service.model.Pricing;
import com.fedex.aggregation.service.model.Shipment;
import com.fedex.aggregation.service.model.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.fedex.aggregation.service.gateway.PricingClient.DEFAULT_PRICING;
import static com.fedex.aggregation.service.gateway.ShipmentClient.DEFAULT_SHIPMENT;
import static com.fedex.aggregation.service.gateway.TrackClient.DEFAULT_TRACK;
import static java.util.Objects.isNull;
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

        final Flux<Pricing> pricingResponseFlux = nonNull(pricing) ?
                pricingGateway.getPricing(pricing)
                        .onErrorReturn(DEFAULT_PRICING)
                : Flux.just(DEFAULT_PRICING).onErrorComplete();

        final Flux<Track> trackResponseFlux = nonNull(track) ?
                trackGateway.getTracking(track)
                        .onErrorReturn(DEFAULT_TRACK)
                : Flux.just(DEFAULT_TRACK).onErrorComplete();

        final Flux<Shipment> shipmentResponseFlux = nonNull(shipments) ?
                shipmentGateway.getShipment(shipments)
                        .onErrorReturn(DEFAULT_SHIPMENT)
                : Flux.just(DEFAULT_SHIPMENT).onErrorComplete();

        return Mono.from(Flux.zip(pricingResponseFlux, shipmentResponseFlux, trackResponseFlux)
                .mapNotNull(r -> createAggregatedResponse(r.getT1().getResponseMap(), r.getT3().getResponseMap(), r.getT2().getResponseMap())));
    }

    private AggregatedResponse createAggregatedResponse(Map<String, Double> p, Map<Long, Track.Status> t, Map<Long, List<String>> s) {
        Map<String, Double> pricing = nonNull(p) && !p.isEmpty() ? p : null;
        Map<Long, Track.Status> track = nonNull(t) && !t.isEmpty() ? t : null;
        Map<Long, List<String>> shipments = nonNull(s) && !s.isEmpty() ? s : null;
        return isNull(pricing) && isNull(track) && isNull(shipments)
                ? null
                : new AggregatedResponse(pricing, track, shipments);
    }
}