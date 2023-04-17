package com.fedex.aggregation.service;

import com.fedex.aggregation.service.model.AggregatedResponse;
import com.fedex.aggregation.service.service.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class Handler {

    private final AggregationService aggregationService;

    private static final Set<String> validCountryCodes = Set.of(Locale.getISOCountries());

    public Handler(@Autowired AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    public Mono<ServerResponse> getAggregation(ServerRequest request) {
        return ok().body(
                aggregationService.getAggregation(
                        getPricingParams(request),
                        getOrderIdParams(request, "track"),
                        getOrderIdParams(request, "shipments")
                ), AggregatedResponse.class);
    }

    private Set<String> getPricingParams(ServerRequest request) {
        var requestedCountryCodes = Arrays.stream(request.queryParam("pricing")
                        .orElse("") //These parameters are all optional and could be missing
                        .split(","))
                .collect(Collectors.toSet());

        requestedCountryCodes.forEach(countryCode -> {
            if (!validCountryCodes.contains(countryCode)) {
                throw new IllegalArgumentException("Invalid ISOCountryCode: " + countryCode);
            }
        });
        return requestedCountryCodes;
    }

    private List<Long> getOrderIdParams(ServerRequest request, String queryParam) {
        return Arrays.stream(request.queryParam(queryParam)
                        .orElse("") //These parameters are all optional and could be missing
                        .split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}
