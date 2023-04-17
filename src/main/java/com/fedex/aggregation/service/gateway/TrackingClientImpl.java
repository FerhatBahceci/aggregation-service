package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.TrackResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrackingClientImpl implements TrackingGateway {

    private final WebClient client;

    public TrackingClientImpl(@Qualifier("shipmentClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Mono<TrackResponse> getTracking(List<Long> orderIds) {
        return !orderIds.isEmpty()
                ? getTracking(orderIds.stream().map(Object::toString).collect(Collectors.joining(",")))
                : Mono.empty();
    }

    private Mono<TrackResponse> getTracking(String orderIds) {
        return client
                .get()
                .uri(builder ->
                        builder.path("/track").queryParam("q", orderIds).build()
                )
                .header("Content-Type", "application/json;charset=UTF-8")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(TrackResponse.class);
    }
}
