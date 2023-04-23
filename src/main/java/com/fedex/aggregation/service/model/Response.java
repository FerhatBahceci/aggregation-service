package com.fedex.aggregation.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fedex.aggregation.service.gateway.OverloadingPreventionHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Response<K, V> extends OverloadingPreventionHandler {
    private Map<K, V> responseMap;

    @JsonCreator
    public Response(Map<K, V> responseMap) {
        this.responseMap = responseMap;
    }

    public static <R extends Response<K, V>, K, V> Map<K, V> merge(List<R> responseList) {
        return responseList.stream().map(Response::getResponseMap)
                .reduce((map1, map2) ->
                        Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (value1, value2) -> value2)))                               // Skip oldest value and select newest
                .orElse(Map.of());
    }

    public Map<K, V> getResponseMap() {
        return responseMap;
    }
}
