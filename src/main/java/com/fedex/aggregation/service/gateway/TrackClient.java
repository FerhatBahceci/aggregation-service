package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import com.fedex.aggregation.service.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/*   .buffer()
    Collect incoming values into multiple List buffers that will be emitted by the returned Flux every bufferingTimespan.
    Discard Support: This operator discards the currently open buffer upon cancellation or error triggered by a data signal.
    Params:
    bufferingTimespan – the duration from buffer creation until a buffer is closed and emitted
    Returns:
    a microbatched Flux of List delimited by the given time span

    .window()
    Split this Flux sequence into multiple Flux windows containing maxSize elements (or less for the final window) and starting
    from the first item. Each Flux window will onComplete once it contains maxSize elements OR it has been open for the given Duration (as measured on the parallel Scheduler).
*/

@Component
public class TrackClient extends QueryParamsCreator implements TrackGateway {
    private static final Logger logger = LoggerFactory.getLogger(TrackClient.class);
    private final WebClient client;
    public static final TrackResponse defaultTrackResponse = new TrackResponse(null);

    public TrackClient(@Qualifier("shipmentWebClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Flux<TrackResponse> getTracking(String orderIds) {
        var executables = super.getExecutableRequests(orderIds);
        if (executables.size() >= cap) {                                            // Entering overloading prevention
            return Flux.just(executables.toArray(new String[executables.size()]))
                    .windowTimeout(5, Duration.ofSeconds(5))                // 1 single request contains q=1,2,3,4,5. The window need to contain 5xq before firing of the calls, The window in question buffers max 5 requests up to 5s from that the window was opened for preventing overloading of provider service
                    .flatMap(windowedQueryParams -> windowedQueryParams.flatMap(this::get).collectList())
                    .doOnNext(trackResponses -> logger.info("Fetched TrackResponses:{}", trackResponses))
                    .map(TrackResponse::mergeTrack)
                    .map(TrackResponse::new);
        } else {                                                                     // Entering ensuring that calls are buffered and executed within 5s from initial call
            executables = executables.isEmpty()
                    ? Arrays.stream(pollAllQueryParams().toArray()).map(Object::toString).collect(Collectors.toSet()) // In case of any other thread populating the queryParamsQueue, we will ensure to load these params again (hopefully they have not exceeded the cap limit)
                    : executables;
            return Flux.just(executables.toArray(new String[executables.size()]))
                    .buffer(Duration.ofSeconds(5))
                    .delayElements(Duration.ofSeconds(5))
                    .flatMap(bufferedQueryParams -> get(StringUtil.getString(bufferedQueryParams)))
                    .doOnNext(shipmentResponse -> logger.info("Fetched TrackResponses:{}", shipmentResponse));
        }
    }

    public Mono<TrackResponse> get(String orderIds) {
        logger.info("Calling Track API with following orderIds={}", orderIds);
        return (!orderIds.isEmpty() ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/track").queryParam("q", orderIds).build()
                        )
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .retrieve()
                        .bodyToMono(TrackResponse.class)
                        .onErrorReturn(new TrackResponse(Map.of()))
                : Mono.empty());
    }
}
