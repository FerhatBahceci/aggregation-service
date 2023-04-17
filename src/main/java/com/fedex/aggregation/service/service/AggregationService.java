package com.fedex.aggregation.service.service;

/*This could possibly be a GraphQL endpoint that puzzles all the calls together by implementing datasources resolvers and schemas.*/

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackingGateway;
import com.fedex.aggregation.service.model.AggregatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Set;
import static java.util.Objects.nonNull;

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
            List<Long> track,
            List<Long> shipments) {
        return pricingGateway.getPricing(pricing)
                .flatMap(pricingResponse -> trackingGateway.getTracking(track)
                        .flatMap(trackResponse -> shipmentGateway.getShipment(shipments)
                                .map(shipmentResponse ->
                                        new AggregatedResponse(
                                                nonNull(pricingResponse) ? pricingResponse.getPricing() : null,
                                                nonNull(trackResponse) ? trackResponse.getTrack() : null,
                                                nonNull(shipmentResponse) ? shipmentResponse.getShipments() : null
                                        )
                                )
                        )
                );
    }
}
