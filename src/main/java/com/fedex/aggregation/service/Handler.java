package com.fedex.aggregation.service;

import com.fedex.aggregation.service.model.AggregatedResponse;
import com.fedex.aggregation.service.service.AggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static com.fedex.aggregation.service.util.StringUtil.getLongList;
import static com.fedex.aggregation.service.util.StringUtil.getStringSet;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class Handler {

    private static final Logger logger = LoggerFactory.getLogger(AggregationService.class);
    private final AggregationService aggregationService;

    private static final Set<String> validCountryCodes = Set.of(Locale.getISOCountries());

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

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

    private String getOrderIdParams(ServerRequest request, String queryParam) {
        List<String> validatedOrderIds = new ArrayList<>();
        var orderIdQueryParams = request.queryParam(queryParam).orElse(""); //These parameters are all optional and could be missing
        if (!orderIdQueryParams.isBlank()) {
            List<Long> orderIds = getLongList(orderIdQueryParams);
            validatedOrderIds = validateOrderIds(orderIds);
        }
        return String.join(",", validatedOrderIds);
    }

    private String getPricingParams(ServerRequest request) {
        Set<String> countryCodes = Set.of();
        var pricingQueryParam = request.queryParam("pricing").orElse(""); //These parameters are all optional and could be missing
        if (!pricingQueryParam.isBlank()) {
            countryCodes = validateCountryCodes(getStringSet(pricingQueryParam)); // Ensures distinct countryCodes.
        }
        return String.join(",", countryCodes);
    }

    private Set<String> validateCountryCodes(Set<String> countryCodes) {
        countryCodes.forEach(countryCode -> {
            if (!validCountryCodes.contains(countryCode)) {
                log.info("IllegalArgument, Invalid ISOCountryCode: {}" + countryCode);
            }
        });
        return countryCodes;
    }

    private List<String> validateOrderIds(List<Long> orderIds) {
        orderIds.forEach(orderId -> {
            if (String.valueOf(orderId).length() != 9) {
                log.info("IllegalArgument, Invalid OrderIde: {}", orderId);
            }
        });
        return orderIds.stream().map(Object::toString).collect(Collectors.toList());
    }
}
