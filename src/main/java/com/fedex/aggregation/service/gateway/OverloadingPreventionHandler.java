package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.Response;
import com.fedex.aggregation.service.util.StringUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fedex.aggregation.service.util.StringUtil.getStringSetFromString;

public abstract class OverloadingPreventionHandler {
    private static final int cap = 5;
    private ConcurrentLinkedQueue<String> queryParamsQueue = new ConcurrentLinkedQueue<>();

    public <R extends Response<R, K, V>, K, V> Flux<R> get(String queryParams, Function<String, Mono<R>> getCallback, Function<Map<K, V>, R> responseConstructor) {
        var executables = getExecutableRequests(queryParams);
        return executables.size() >= cap
                ?
                Flux.just(executables.toArray(new String[executables.size()]))
                        .windowTimeout(5, Duration.ofSeconds(5))                                                  // 1 single request contains q=1,2,3,4,5. The window need to contain 5xq before firing of the calls, The window in question buffers max 5 requests up to 5s from that the window was opened for preventing overloading of provider service
                        .flatMap(windowedQueryParams -> windowedQueryParams.flatMap(getCallback).collectList())
                        .map(Response::merge)
                        .map(responseConstructor::apply)
                :
                Flux.just(getCurrentExecutables(executables))
                        .buffer(Duration.ofSeconds(5))
                        .delayElements(Duration.ofSeconds(5))
                        .flatMap(bufferedQueryParams -> getCallback.apply(StringUtil.getConcatenatedStringFromList(bufferedQueryParams)));
    }

    private Set<String> getExecutableRequests(String ids) {
        queryParamsQueue.addAll(getStringSetFromString(ids));               // Splits the q= queryParam into multiple elements, String set ensures distinct values for the request
        List<String> tmpExecutables = new ArrayList<>();

        while (queryParamsQueue.size() >= cap) {
            tmpExecutables.add(queryParamsQueue.poll());                    //Adds all comma separated request values, one by one to tmpExecutables until cap is reached
        }

        Set<String> executables = new HashSet<>();
        while (tmpExecutables.size() >= cap) {                              // Request calls that are not complete are stored on local instance of aggregation-service in ConcurrentLinkedQueue<String> queryParamsQueue
            List<String> singleRequest = tmpExecutables.subList(0, cap);
            String request = String.join(",", singleRequest);       // Concatenates into a single request with 5 deli-metered values
            executables.add(request);
            tmpExecutables.removeAll(singleRequest);
        }

        return executables;
    }

    private String[] getCurrentExecutables(Set<String> executables) {
        var currentExecutables = executables.isEmpty()
                ? Arrays.stream(pollAllQueryParams().toArray()).map(Object::toString).collect(Collectors.toSet()) // In case of any other thread populating the queryParamsQueue, we will ensure to load these params again (hopefully they have not exceeded the cap limit)
                : executables;
        return currentExecutables.toArray(new String[currentExecutables.size()]);
    }

    private Set<String> pollAllQueryParams() {
        List<String> tmpExecutables = new ArrayList<>();
        while (!queryParamsQueue.isEmpty()) {
            tmpExecutables.add(queryParamsQueue.poll());
        }
        return new HashSet<>(tmpExecutables);
    }
}
