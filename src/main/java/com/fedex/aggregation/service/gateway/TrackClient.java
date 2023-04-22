package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;


@Component
public class TrackClient extends QueryParamsCreator implements TrackGateway {
    private static final Logger logger = LoggerFactory.getLogger(TrackClient.class);
    private final WebClient client;
    public static final List<TrackResponse> defaultTrackResponse = List.of();

    public TrackClient(@Qualifier("shipmentWebClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Flux<List<TrackResponse>> getTracking(String orderIds) {
        var executables = super.getExecutableRequests(orderIds);

        if (!executables.isEmpty()) {

            return Flux.just(executables.toArray(new String[executables.size()]))
                    .windowTimeout(1, Duration.ofSeconds(5))                // 1 single request contains q=1,2,3,4,5. The window in question buffers max 5 requests up to 5s from that the window was opened
                    .flatMap(stringFlux -> stringFlux.flatMap(this::get).collectList());
        } else {
            return Flux.just(executables.toArray(new String[executables.size()]))
                    .windowTimeout(1, Duration.ofSeconds(5))
                    /*  .delayUntil() */   // DelayUntil predicate of checking !executables.isEmpty() .delayUntil(-> predicate is matched)
                    .flatMap(stringFlux -> stringFlux.flatMap(this::get).collectList());
        }
    }

        /*flux.doOnComplete(() -> {
                    logger.info("COMPLETED!");
                })
                .doOnNext(trackResponse ->
                        {
*//*
                            callbackQueue.add(trackResponse);
*//*
                            logger.info("This is the subscribed TrackResponse:{}", trackResponse);
                        }
                );*/
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
                        .onErrorReturn(new TrackResponse(Map.of()))
/*
                        .log()
*/
                : Mono.empty());
    }
}
