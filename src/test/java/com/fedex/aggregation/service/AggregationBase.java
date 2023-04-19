package com.fedex.aggregation.service;

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackGateway;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
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
    TrackGateway trackGateway;

    @MockBean
    ShipmentGateway shipmentGateway;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = this.port;

        when(pricingGateway.getPricing(any())).thenReturn(createError());
        when(trackGateway.getTracking(any())).thenReturn(createError());
        when(shipmentGateway.getShipment(any())).thenReturn(createError());
    }

    private <T> Mono<T> createError() {
        return Mono.error(new IllegalArgumentException("Error from fedex BE service!"));
    }
}
