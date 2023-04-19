package com.fedex.aggregation.service;

import com.fedex.aggregation.service.model.PricingResponse;
import com.fedex.aggregation.service.model.ShipmentResponse;
import com.fedex.aggregation.service.model.TrackResponse;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.List;

interface TestData {
    Long ORDER_ID_1 = 109347263L;
    Long ORDER_ID_2 = 123456891L;
    Mono<PricingResponse> PRICING_RESPONSE = Mono.just(new PricingResponse(new HashMap<>() {{
        put("NL",  14.242090605778d);
        put("CN", 20.503467806384d);
    }}));

    Mono<TrackResponse> TRACK_RESPONSE = Mono.just(new TrackResponse(
            new HashMap<>() {{
                put(ORDER_ID_1, null);
                put(ORDER_ID_2, TrackResponse.Status.COLLECTING);
            }}
    ));

    Mono<ShipmentResponse> SHIPMENT_RESPONSE = Mono.just(new ShipmentResponse(
            new HashMap<>() {{
                put(ORDER_ID_1, List.of("box", "box", "pallet"));
                put(ORDER_ID_2, null);
            }}
    ));
}
