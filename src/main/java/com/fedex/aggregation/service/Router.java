package com.fedex.aggregation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/* Reason for using functional approach, it easier to locate and navigate the actual API of the service rather than having to jump between Controller classes.
 It gives a more concise and clear API overview if Handlers created separately from the Routers for extracting/validating input IMO */

@Configuration
public class Router {

    private final Handler handler;

    public Router(@Autowired Handler handler) {
        this.handler = handler;
    }

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route(GET("/aggregation"), handler::getAggregation);
    }
}