package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TrackClientImpl implements TrackGateway {
    private final WebClient client;
    private final OverLoadingPreventionHandler<TrackResponse> overloadingPreventionHandler = new OverLoadingPreventionHandler<>();

    public TrackClientImpl(@Qualifier("shipmentClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Mono<TrackResponse> getTracking(String orderIds) {
        return !orderIds.isBlank() ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/track").queryParam("q", orderIds).build()
                        )
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .retrieve()
                        .bodyToMono(TrackResponse.class)
                : Mono.empty();
    }
}