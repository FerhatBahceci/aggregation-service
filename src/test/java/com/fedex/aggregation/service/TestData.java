package com.fedex.aggregation.service;

import com.fedex.aggregation.service.model.Pricing;
import com.fedex.aggregation.service.model.Shipment;
import com.fedex.aggregation.service.model.Track;
import reactor.core.publisher.Flux;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

interface TestData {
    Long ORDER_ID_1 = 109347263L;
    Long ORDER_ID_2 = 123456891L;
    Long ORDER_ID_3 = 123456894L;
    Long ORDER_ID_4 = 123456896L;

    Long ORDER_ID_5 = 123456898L;

    Set<String> ORDER_IDS = Set.of(ORDER_ID_1, ORDER_ID_2, ORDER_ID_3, ORDER_ID_4, ORDER_ID_5).stream().map(Objects::toString).collect(Collectors.toSet());

    Flux<Pricing> PRICING_RESPONSE = Flux.just(new Pricing(new HashMap<>() {{
        put("NL", 14.242090605778d);
        put("CN", 20.503467806384d);
    }}));

    Flux<Track> TRACK_RESPONSE = Flux.just(new Track(
            new HashMap<>() {{
                put(ORDER_ID_1, null);
                put(ORDER_ID_2, Track.Status.COLLECTING);
            }}
    ));

    Flux<Shipment> SHIPMENT_RESPONSE = Flux.just(new Shipment(
            new HashMap<>() {{
                put(ORDER_ID_1, List.of("box", "box", "pallet"));
                put(ORDER_ID_2, null);
            }}
    ));
}
