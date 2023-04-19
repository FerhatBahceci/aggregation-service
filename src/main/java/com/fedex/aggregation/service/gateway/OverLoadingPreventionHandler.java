package com.fedex.aggregation.service.gateway;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OverLoadingPreventionHandler<T> {
    private final ConcurrentLinkedQueue<Mono<T>> callQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> queryParamsQueue = new ConcurrentLinkedQueue<>();

    public Flux<T> getBulkCallsOrWait(Set<String> queryParams) {

        // 1. Check in callQueue if there is 5 calls to be made? It should not be since we should consume 5 calls into one bulked call once cap of 5 is reached and store it in the callQueue

        // 2. Check queryParams IF it is possible to pack N amounts of calls and put them in the callQueue. 5 queryParams per call, for instance /aggregation?track=123456789,123456799,123456799,123456799,123456799 is one call Mono<T> that should be put into the queue.

        // 3.


        if (queryParams.size() % 5 == 0) {


        } else {

        }

        return Flux.empty();
    }

}
