package com.fedex.aggregation.service.gateway;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.fedex.aggregation.service.gateway.SingleRequest.create;

public class OverLoadingPreventionHandler<T> {
    private static final int cap = 5;
    private final ConcurrentLinkedQueue<Mono<T>> callbackQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> queryParamsQueue = new ConcurrentLinkedQueue<>();

    public Flux<T> getBulkCallsOrSuspend(Function<String, Mono<T>> callbackConstructor, Set<String> queryParams) {

        Flux<T> response = Flux.empty();
        List<String> tmpQueryParams = new ArrayList<>();
        queryParamsQueue.addAll(queryParams);

        while (queryParamsQueue.size() >= cap) {
            var tmpPollQueryParam = queryParamsQueue.poll();
            tmpQueryParams.add(tmpPollQueryParam);
        }

        while (tmpQueryParams.size() >= cap) { //Here we are ensuring that maximum 5 params are included in the optional q?=
            var currentTmpQueryParams = tmpQueryParams.subList(0, 5);
            SingleRequest request = create(currentTmpQueryParams);
            Mono<T> preparedCall = callbackConstructor.apply(request.getQueryParamString());
            callbackQueue.offer(preparedCall);
            tmpQueryParams.removeAll(currentTmpQueryParams);
        }

        if (callbackQueue.size() >= cap) {  //Here we are ensuring that we are merging 5 publishers into one call
            List<Mono<T>> preparedCalls = Stream.of(0, 1, 2, 3, 4).map(i -> callbackQueue.poll()).toList();  //TODO make a nicer way of iterating exactly CAP amount of times
            response = Flux.merge(preparedCalls);
        }

        return response;
    }
}
