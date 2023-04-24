package com.fedex.aggregation.service;

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackGateway;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import reactor.core.publisher.Flux;

import static com.fedex.aggregation.service.TestData.PRICING_RESPONSE;
import static com.fedex.aggregation.service.TestData.SHIPMENT_RESPONSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
abstract class AggregationBase {

    @LocalServerPort
    private Integer port = 0;

    @MockBean
    private PricingGateway pricingGateway;

    @MockBean
    private TrackGateway trackGateway;

    @MockBean
    private ShipmentGateway shipmentGateway;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = this.port;

        when(pricingGateway.getPricing(any())).thenReturn(PRICING_RESPONSE);
        when(shipmentGateway.getShipment(any())).thenReturn(SHIPMENT_RESPONSE);
        when(trackGateway.getTracking(any())).thenReturn(createError());
    }

    private <T> Flux<T> createError() {
        return Flux.error(new IllegalArgumentException("Error from fedex BE service!"));
    }
}
