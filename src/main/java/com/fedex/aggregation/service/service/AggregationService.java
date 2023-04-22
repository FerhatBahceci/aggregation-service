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
import java.util.List;
import static com.fedex.aggregation.service.gateway.PricingClient.defaultPricingResponse;
import static com.fedex.aggregation.service.gateway.ShipmentClient.defaultShipmentResponse;
import static com.fedex.aggregation.service.gateway.TrackClient.defaultTrackResponse;
import static com.fedex.aggregation.service.model.PricingResponse.mergePricing;
import static com.fedex.aggregation.service.model.ShipmentResponse.mergeShipments;
import static com.fedex.aggregation.service.model.TrackResponse.mergeTrack;
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

    public Flux<AggregatedResponse> getAggregation(
            String pricing,
            String track,
            String shipments) {

        final Flux<List<PricingResponse>> pricingResponseFlux = nonNull(pricing) ?
                pricingGateway.getPricing(pricing)
                        .switchIfEmpty(Mono.just(defaultPricingResponse))
                        .onErrorReturn(defaultPricingResponse)
                : Flux.just(defaultPricingResponse);

        final Flux<List<TrackResponse>> trackResponseFlux = nonNull(track) ?
                trackGateway.getTracking(track)
                        .switchIfEmpty(Mono.just(defaultTrackResponse))
                        .onErrorReturn(defaultTrackResponse)
                : Flux.just(defaultTrackResponse);

        final Flux<List<ShipmentResponse>> shipmentResponseFlux = nonNull(shipments) ?
                shipmentGateway.getShipment(shipments)
                        .switchIfEmpty(Mono.just(defaultShipmentResponse))
                        .onErrorReturn(defaultShipmentResponse)
                : Flux.just(defaultShipmentResponse);

        return Flux.zip(pricingResponseFlux, shipmentResponseFlux, trackResponseFlux)
                .mapNotNull(r -> {
                    var agg = new AggregatedResponse();
                    agg.setPricing(mergePricing(r.getT1()));
                    agg.setShipments(mergeShipments(r.getT2()));
                    agg.setTrack(mergeTrack(r.getT3()));
                    return agg;
                });
    }
}