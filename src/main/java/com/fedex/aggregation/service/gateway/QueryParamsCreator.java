package com.fedex.aggregation.service.gateway;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.fedex.aggregation.service.util.StringUtil.getStringSetFromString;

abstract class QueryParamsCreator {

    public static final int cap = 5;
    private ConcurrentLinkedQueue<String> queryParamsQueue = new ConcurrentLinkedQueue<>();

    public Set<String> getExecutableRequests(String ids) {
        queryParamsQueue.addAll(getStringSetFromString(ids));  // Splits the q= queryParam into multiple elements, String set ensures distinct values for the request
        List<String> tmpExecutables = new ArrayList<>();

        while (queryParamsQueue.size() >= cap) {
            tmpExecutables.add(queryParamsQueue.poll());  //Adds all comma separated request values, one by one to tmpExecutables until cap is reached
        }

        Set<String> executables = new HashSet<>();
        while (tmpExecutables.size() >= cap) {   // Request calls that are not complete are stored on local instance of aggregation-service in ConcurrentLinkedQueue<String> queryParamsQueue
            List<String> singleRequest = tmpExecutables.subList(0, cap);
            String request = String.join(",", singleRequest); // Concatenates into a single request with 5 deli-metered values
            executables.add(request);
            tmpExecutables.removeAll(singleRequest);
        }

        return executables;
    }
    public Set<String> pollAllQueryParams() {
        List<String> tmpExecutables = new ArrayList<>();
        while (!queryParamsQueue.isEmpty()){
            tmpExecutables.add(queryParamsQueue.poll());
        }
        return new HashSet<>(tmpExecutables);
    }
}
