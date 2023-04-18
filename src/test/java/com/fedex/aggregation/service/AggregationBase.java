package com.fedex.aggregation.service;

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackingGateway;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
abstract class AggregationBase {

    @LocalServerPort
    Integer port = 0;

    @MockBean
    PricingGateway pricingGateway;

    @MockBean
    TrackingGateway trackingGateway;

    @MockBean
    ShipmentGateway shipmentGateway;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = this.port;

        when(pricingGateway.getPricing(any())).thenReturn(createError());
        when(trackingGateway.getTracking(any())).thenReturn(createError());
        when(shipmentGateway.getShipment(any())).thenReturn(createError());
    }

    private <T> Flux<T> createError() {
        return Flux.error(new IllegalArgumentException("Error from fedex BE service!"));
    }
}
