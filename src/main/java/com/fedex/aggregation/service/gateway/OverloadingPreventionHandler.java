package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.Response;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.fedex.aggregation.service.util.StringUtil.getConcatenatedStringFromList;
import static com.fedex.aggregation.service.util.StringUtil.getStringSetFromString;
import static java.util.Objects.nonNull;


public abstract class OverloadingPreventionHandler {
    private static final int cap = 5;
    private ConcurrentLinkedQueue<String> queryParamsQueue = new ConcurrentLinkedQueue<>();

    public <R extends Response<K, V>, K, V> Flux<R> get(String queryParams, Function<String, Mono<R>> getCallback, Function<Map<K, V>, R> responseConstructor) {
        var executables = getExecutableRequests(queryParams);
        return Flux.just(executables.toArray(new String[executables.size()]))                                      // It is unclear from the task description if it should actually be 5x5 q=1,2,3,4,5   5x1 = or q1=1, q2=2 q3=3, q4=4, q5=5.
                .windowTimeout(1, Duration.ofSeconds(5))                                                         // 1 single request contains q=1,2,3,4,5. The window need to contain 5xq before firing of the calls, The window in question buffers max 5 requests up to 5s from that the window was opened for preventing overloading of provider service
                .flatMap(windowedQueryParams -> windowedQueryParams.flatMap(getCallback).collectList())
                .map(Response::merge)
                .map(responseConstructor);
    }

    private Set<String> getExecutableRequests(String queryParams) {
        queryParamsQueue.addAll(getStringSetFromString(queryParams));                               // Splits the q= queryParam into multiple elements, String set ensures distinct values for the request
        Set<String> executables = new HashSet<>();
        List<String> tmpExecutables = new ArrayList<>();

        if (queryParamsQueue.size() >= cap) {
            while (queryParamsQueue.size() >= cap && executables.size() <= cap) {                    // 5 x q(q1,q2,q3,q4,q5) = 25 queryParams in q=
                IntStream.range(0, cap).forEach(i -> tmpExecutables.add(queryParamsQueue.poll()));   //Adds all comma separated request values, one by one to tmpExecutables until cap is reached
                List<String> singleRequest = tmpExecutables.subList(0, cap);                         //  1 single request contains q=1,2,3,4,5
                String request = getConcatenatedStringFromList(singleRequest);                       // Concatenates into a single request with 5 deli-metered values
                executables.add(request);
                tmpExecutables.removeAll(singleRequest);
            }
            queryParamsQueue.addAll(tmpExecutables);                // If any queryParams does not manage to get included in the executables, we will put them back for next Call. The result will however not be delivered to the initial subscriber in question.
        } else {
            while (!queryParamsQueue.isEmpty()) {
                tmpExecutables.add(queryParamsQueue.poll());
            }
            String lessThanCapInOneGo = getConcatenatedStringFromList(tmpExecutables);
            if (nonNull(lessThanCapInOneGo)) {
                executables.add(lessThanCapInOneGo);
            }
        }
        return executables;
    }
}
