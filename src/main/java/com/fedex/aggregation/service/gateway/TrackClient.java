package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

@Component
public class TrackClient extends OverloadingPreventionHandler implements TrackGateway {
    private static final Logger logger = LoggerFactory.getLogger(TrackClient.class);
    private final WebClient client;
    public static final Track DEFAULT_TRACK = new Track(null);

    public TrackClient(@Qualifier("shipmentWebClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Flux<Track> getTracking(String orderIds) {
        return get(orderIds, this::get, Track::new)
                .doOnNext(trackResponse -> logger.info("Fetched TrackResponse:{}", trackResponse));
    }

    public Mono<Track> get(String orderIds) {
        logger.info("Calling Track API with following orderIds={}", orderIds);
        return (!orderIds.isEmpty() ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/track").queryParam("q", orderIds).build()
                        )
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .retrieve()
                        .bodyToMono(Track.class)
                        .onErrorReturn(new Track(Map.of()))
                : Mono.empty());
    }
}
