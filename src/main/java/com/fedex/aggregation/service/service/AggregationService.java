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

    public Mono<AggregatedResponse> getAggregation(
            String pricing,
            String track,
            String shipments) {

        final Flux<List<PricingResponse>> pricingResponseFlux = nonNull(pricing) ?
                pricingGateway.getPricing(pricing)
                        .onErrorReturn(defaultPricingResponse)
                : Flux.empty();

        final Flux<List<TrackResponse>> trackResponseFlux = nonNull(track) ?
                trackGateway.getTracking(track)
                        .onErrorReturn(defaultTrackResponse)
                : Flux.empty();

        final Flux<List<ShipmentResponse>> shipmentResponseFlux = nonNull(shipments) ?
                shipmentGateway.getShipment(shipments)
                        .onErrorReturn(defaultShipmentResponse)
                : Flux.empty();

        // TODO suspend zip until all responses has returned, if any API throws error that should be considered as empty/null response
        return Mono.from(Flux.zip(pricingResponseFlux, shipmentResponseFlux, trackResponseFlux)
                .mapNotNull(r -> {
                    if (!r.getT1().isEmpty() && !r.getT2().isEmpty() && !r.getT3().isEmpty()) {
                        var agg = new AggregatedResponse();
                        agg.setPricing(mergePricing(r.getT1()));
                        agg.setShipments(mergeShipments(r.getT2()));
                        agg.setTrack(mergeTrack(r.getT3()));
                        return agg;
                    } else {
                        return null;
                    }
                }));
    }
}