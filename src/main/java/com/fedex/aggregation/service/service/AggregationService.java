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

import static com.fedex.aggregation.service.gateway.PricingClient.DEFAULT_PRICING;
import static com.fedex.aggregation.service.gateway.ShipmentClient.DEFAULT_SHIPMENT;
import static com.fedex.aggregation.service.gateway.TrackClient.DEFAULT_TRACK;
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
                : Flux.empty();

        final Flux<Track> trackResponseFlux = nonNull(track) ?
                trackGateway.getTracking(track)
                        .onErrorReturn(DEFAULT_TRACK)
                : Flux.empty();

        final Flux<Shipment> shipmentResponseFlux = nonNull(shipments) ?
                shipmentGateway.getShipment(shipments)
                        .onErrorReturn(DEFAULT_SHIPMENT)
                : Flux.empty();

        return Mono.from(Flux.zip(pricingResponseFlux, shipmentResponseFlux, trackResponseFlux)
                .mapNotNull(r -> {
                    var agg = new AggregatedResponse();
                    agg.setPricing(r.getT1().getResponseMap());
                    agg.setShipments(r.getT2().getResponseMap());
                    agg.setTrack(r.getT3().getResponseMap());
                    return agg;
                }));
    }
}