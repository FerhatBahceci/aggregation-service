package com.fedex.aggregation.service;

import com.fedex.aggregation.service.model.AggregatedResponse;
import com.fedex.aggregation.service.service.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.Arrays;
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

    //If the same value is present multiple times in the query, the response will only contain it once --> Using Set for all paramBuilders for ensuring distinct values

    private Set<Long> getOrderIdParams(ServerRequest request, String queryParam) {
        Set<Long> orderIds = Set.of();
        var orderIdQueryParams = request.queryParam(queryParam).orElse(""); //These parameters are all optional and could be missing
        if (!orderIdQueryParams.isBlank()) {
            orderIds = Arrays.stream(orderIdQueryParams
                            .split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
        }
        return orderIds;
    }

    private Set<String> getPricingParams(ServerRequest request) {
        Set<String> countryCodes = Set.of();
        var pricingQueryParam = request.queryParam("pricing").orElse(""); //These parameters are all optional and could be missing
        if (!pricingQueryParam.isBlank()) {
            countryCodes = validateCountryCodes(Arrays.stream(pricingQueryParam.split(",")).collect(Collectors.toSet())); // Ensures distinct countryCodes.
        }
        return countryCodes;
    }

    private Set<String> validateCountryCodes(Set<String> countryCodes) {
        countryCodes.forEach(countryCode -> {
            if (!validCountryCodes.contains(countryCode))
                throw new IllegalArgumentException("Invalid ISOCountryCode: " + countryCode);
        });
        return countryCodes;
    }

}
