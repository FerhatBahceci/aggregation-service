package com.fedex.aggregation.service.gateway;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.fedex.aggregation.service.gateway.SingleRequest.create;

/*
The OverLoadingPreventionHandler is per instance of aggregation-service. In production, it is most likely that we would have N amount of instances of aggregation-service's up and running.
This would not prevent from overloading the exposed provider API (Fedex BE services). For that, we would need to know more details about the amount of instances for both subscribing (downstream) and publishing (upstream), amount of events emitted, processing frequency etc .
*/

abstract class BulkRequestHandler<T> {
    private static final int cap = 5;
    private final ConcurrentLinkedQueue<Mono<T>> callbackQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> queryParamsQueue = new ConcurrentLinkedQueue<>();

    public void getBulkCallsOrWait(Function<String, Mono<T>> callbackConstructor, Set<String> queryParams, Sinks.Many<T> sink) {

        List<String> tmpQueryParams = new ArrayList<>();
        queryParamsQueue.addAll(queryParams);

        while (queryParamsQueue.size() >= cap) {
            pollFromQueryParamsQueue(tmpQueryParams);
        }

        if (tmpQueryParams.size() >= cap) {
            while (tmpQueryParams.size() >= cap) { //Ensuring that maximum 5 params are included in the optional q?= per call
                tmpQueryParams = limitParamListPerSingleRequest(callbackConstructor, tmpQueryParams);
            }
        } else {
            queryParamsQueue.addAll(tmpQueryParams);
        }

        if (callbackQueue.size() >= cap) {  //Ensuring that we are merging 5 publishers
            List<Mono<T>> tmpCallbackHolder = new ArrayList<>();
            IntStream.range(0, cap).boxed().toList().stream().forEach(i -> tmpCallbackHolder.add(callbackQueue.poll()));
            Flux.merge(tmpCallbackHolder).subscribe(sink::tryEmitNext);
        }
    }

    private void pollFromQueryParamsQueue(List<String> tmpQueryParams) {
        IntStream.range(0, cap).boxed().toList().stream().forEach(i -> {
            var tmpPollQueryParam = queryParamsQueue.poll();
            tmpQueryParams.add(tmpPollQueryParam);
        });
    }

    private List<String> limitParamListPerSingleRequest(Function<String, Mono<T>> callbackConstructor, List<String> tmpQueryParams) {
        var currentTmpQueryParams = tmpQueryParams.subList(0, 5);
        SingleRequest request = create(currentTmpQueryParams);
        Mono<T> preparedCall = callbackConstructor.apply(request.getQueryParamString());
        callbackQueue.offer(preparedCall);
        tmpQueryParams.removeAll(currentTmpQueryParams);
        return tmpQueryParams;
    }
}
