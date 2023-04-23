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

    public Flux<AggregatedResponse> getAggregation(
            String pricing,
            String track,
            String shipments) {
        return pricingGateway.getPricing(pricing)
                .onErrorReturn(DEFAULT_PRICING)
                .flatMap(p -> trackGateway.getTracking(track)
                        .onErrorReturn(DEFAULT_TRACK)
                        .flatMap(t -> shipmentGateway.getShipment(shipments)
                                .onErrorReturn(DEFAULT_SHIPMENT)
                                .map(s -> new AggregatedResponse(p.getResponseMap(), t.getResponseMap(), s.getResponseMap())))
                );
    }
}