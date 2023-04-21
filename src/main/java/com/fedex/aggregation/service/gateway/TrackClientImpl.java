package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static com.fedex.aggregation.service.util.StringUtil.getStringSet;

@Component
public class TrackClientImpl extends BulkRequestHandler<TrackResponse> implements TrackGateway {
    private static final Logger logger = LoggerFactory.getLogger(TrackClientImpl.class);
    private final WebClient client;
    private final Sinks.Many<TrackResponse> trackSink;

    private final Flux<TrackResponse> flux;
    public static final TrackResponse defaultTrackResponse = new TrackResponse(null);

    public TrackClientImpl(@Qualifier("shipmentClient") WebClient client,
                           @Autowired Sinks.Many<TrackResponse> trackSink,
                           @Autowired Flux<TrackResponse> flux) {
        this.client = client;
        this.trackSink = trackSink;
        this.flux = flux;
    }

    @Override
    public Flux<TrackResponse> getTracking(String orderIds) {
        getBulkCallsOrWait(this::get, getStringSet(orderIds), trackSink);
        return flux;
    }

    public Mono<TrackResponse> get(String orderIds) {
        logger.info("Calling Track API with following orderIds={}", orderIds);
        return (!orderIds.isBlank() ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/track").queryParam("q", orderIds).build()
                        )
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .retrieve()
                        .bodyToMono(TrackResponse.class)
                        .onErrorReturn(defaultTrackResponse)
/*
                        .log()
*/
                : Mono.empty());
    }
}
