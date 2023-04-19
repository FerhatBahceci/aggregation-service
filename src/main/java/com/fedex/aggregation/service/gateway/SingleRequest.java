package com.fedex.aggregation.service.gateway;

import java.util.List;

public class SingleRequest {
    private String request1;

    private String request2;

    private String request3;
    private String request4;

    private String request5;

    public SingleRequest(String request1, String request2, String request3, String request4, String request5) {
        this.request1 = request1;
        this.request2 = request2;
        this.request3 = request3;
        this.request4 = request4;
        this.request5 = request5;
    }

    public SingleRequest() {
    }

    public String getQueryParamString() {
        return String.join(",", request1, request2, request3, request4, request5);
    }

    public static SingleRequest create(List<String> queryParams) {
        return !queryParams.isEmpty() ?
                new SingleRequest(
                        queryParams.get(0),
                        queryParams.get(1),
                        queryParams.get(2),
                        queryParams.get(3),
                        queryParams.get(4))
                : new SingleRequest();
    }
}
