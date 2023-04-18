package com.fedex.aggregation.service;

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackingGateway;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import static com.fedex.aggregation.service.DummyData.*;
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

        when(pricingGateway.getPricing(COUNTRY_CODES)).thenReturn(PRICING_RESPONSE);
        when(trackingGateway.getTracking(ORDER_ID_1_2)).thenReturn(TRACK_RESPONSE);
        when(shipmentGateway.getShipment(ORDER_ID_1_2)).thenReturn(SHIPMENT_RESPONSE);
    }
}
