package com.fedex.aggregation.service.service;

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackGateway;
import com.fedex.aggregation.service.model.AggregatedResponse;
import com.fedex.aggregation.service.model.PricingResponse;
import com.fedex.aggregation.service.model.ShipmentResponse;
import com.fedex.aggregation.service.model.TrackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.fedex.aggregation.service.gateway.PricingClientImpl.defaultPricingResponse;
import static com.fedex.aggregation.service.gateway.ShipmentClientImpl.defaultShipmentResponse;
import static com.fedex.aggregation.service.gateway.TrackClientImpl.defaultTrackResponse;
import static java.util.Objects.nonNull;

@Service
public class AggregationService {
    private static final Logger logger = LoggerFactory.getLogger(AggregationService.class);
    private final PricingGateway pricingGateway;
    private final ShipmentGateway shipmentGateway;
    private final TrackGateway trackGateway;
    public static AggregatedResponse defaultAggregatedResponse = new AggregatedResponse();

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

        if (nonNull(pricing) || nonNull(track) || nonNull(shipments)) {
            final Flux<PricingResponse> pricingResponseFlux = nonNull(pricing) ?
                    pricingGateway.getPricing(pricing)
                            .switchIfEmpty(Mono.just(defaultPricingResponse))
                            .onErrorReturn(defaultPricingResponse)
                    : Flux.just(defaultPricingResponse);

            final Flux<TrackResponse> trackResponseFlux = nonNull(track) ?
                    trackGateway.getTracking(track)
                            .switchIfEmpty(Mono.just(defaultTrackResponse))
                            .onErrorReturn(defaultTrackResponse)
                    : Flux.just(defaultTrackResponse);

            final Flux<ShipmentResponse> shipmentResponseFlux = nonNull(shipments) ?
                    shipmentGateway.getShipment(shipments)
                            .switchIfEmpty(Mono.just(defaultShipmentResponse))
                            .onErrorReturn(defaultShipmentResponse)
                    : Flux.just(defaultShipmentResponse);

 /*           return Flux.zip(pricingResponseFlux, trackResponseFlux, shipmentResponseFlux)
                    .flatMapSequential(response -> {
                        var agg = new AggregatedResponse();
                        agg.setPricing(response.getT1().getPricing());
                        agg.setTrack(response.getT2().getTrack());
                        agg.setShipments(response.getT3().getShipments());
                        logger.info("I AM CONSTRUCTING AGGREGATED RESPONSE:{}", agg);
                       return Mono.just(agg);
                    },8);*/

            return Flux.just(new AggregatedResponse())
                    .zipWith(pricingResponseFlux)
                    .mapNotNull(p -> p.getT1().setPricing(p.getT2().getPricing()))
                    .zipWith(trackResponseFlux)
                    .mapNotNull(t -> t.getT1().setTrack(t.getT2().getTrack()))
                    .zipWith(shipmentResponseFlux)
                    .map(s -> s.getT1().setShipments(s.getT2().getShipments()))
                    .log();
        } else {
            return Flux.just(defaultAggregatedResponse);
        }
    }
}