package com.fedex.aggregation.service;

import com.fedex.aggregation.service.model.Pricing;
import com.fedex.aggregation.service.model.Shipment;
import reactor.core.publisher.Flux;
import java.util.HashMap;
import java.util.List;

interface TestData {
    Long ORDER_ID_1 = 109347263L;
    Long ORDER_ID_2 = 123456891L;

    Flux<Pricing> PRICING_RESPONSE = Flux.just(new Pricing(new HashMap<>() {{
        put("NL", 14.242090605778d);
        put("CN", 20.503467806384d);
    }}));

    Flux<Shipment> SHIPMENT_RESPONSE = Flux.just(new Shipment(
            new HashMap<>() {{
                put(ORDER_ID_1, List.of("box", "box", "pallet"));
                put(ORDER_ID_2, null);
            }}
    ));
}
