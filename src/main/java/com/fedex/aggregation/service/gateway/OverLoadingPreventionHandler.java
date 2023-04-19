package com.fedex.aggregation.service.gateway;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.fedex.aggregation.service.gateway.SingleRequest.create;

/*
The OverLoadingPreventionHandler is per instance of aggregation-service. In production, it is most likely that we would have X amount of aggregation-service's up and running.
This would then not prevent from overloading the exposed provider API (Fedex BE services). For that we would need to know more details about the amount of instances for both subscribing (downstream) and publishing (upstream) side, amount of events emitted, processing frequency etc .
 Backpressure using Sink could be handled for instance programmatically letting the upstream tell the downstream when it is ready to produce/emit elements to the subscriber so that the subscriber is able to process that emitted throughput.
*/

public class OverLoadingPreventionHandler<T> {
    private static final int cap = 5;
    private final ConcurrentLinkedQueue<Mono<T>> callbackQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> queryParamsQueue = new ConcurrentLinkedQueue<>();

    public Flux<T> getBulkCallsOrSuspend(Function<String, Mono<T>> callbackConstructor, Set<String> queryParams) {

        Flux<T> response = Flux.empty();
        List<String> tmpQueryParams = new ArrayList<>();
        queryParamsQueue.addAll(queryParams);

        while (queryParamsQueue.size() >= cap) {
            pollFromQueryParamsQueue(tmpQueryParams);
        }

        while (tmpQueryParams.size() >= cap) { //Here we are ensuring that maximum 5 params are included in the optional q?=
            limitParamListPerSingleRequest(callbackConstructor, tmpQueryParams);
        }

        if (callbackQueue.size() >= cap) {  //Here we are ensuring that we are merging 5 publishers into one call
            response = mergeCalls();
        }
        return response;
    }

    private void pollFromQueryParamsQueue(List<String> tmpQueryParams) {
        var tmpPollQueryParam = queryParamsQueue.poll();
        tmpQueryParams.add(tmpPollQueryParam);
    }

    private void limitParamListPerSingleRequest(Function<String, Mono<T>> callbackConstructor, List<String> tmpQueryParams) {
        var currentTmpQueryParams = tmpQueryParams.subList(0, 5);
        SingleRequest request = create(currentTmpQueryParams);
        Mono<T> preparedCall = callbackConstructor.apply(request.getQueryParamString());
        callbackQueue.offer(preparedCall);
        tmpQueryParams.removeAll(currentTmpQueryParams);
    }

    private Flux<T> mergeCalls() {
        List<Mono<T>> preparedCalls = IntStream.range(0, cap).boxed().toList().stream().map(i -> callbackQueue.poll()).toList();
        return Flux.merge(preparedCalls);
    }
}
