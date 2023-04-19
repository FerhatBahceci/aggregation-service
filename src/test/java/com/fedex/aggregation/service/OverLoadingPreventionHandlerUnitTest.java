/*
package com.fedex.aggregation.service;

import com.fedex.aggregation.service.gateway.OverLoadingPreventionHandler;
import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackingGateway;
import com.fedex.aggregation.service.model.PricingResponse;
import com.fedex.aggregation.service.model.ShipmentResponse;
import com.fedex.aggregation.service.model.TrackResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OverLoadingPreventionHandlerUnitTest {

    @LocalServerPort
    Integer port = 0;
    @MockBean
    PricingGateway pricingGateway;

    @MockBean
    TrackingGateway trackingGateway;

    @MockBean
    ShipmentGateway shipmentGateway;

    private final OverLoadingPreventionHandler<PricingResponse> pricingHandler = new OverLoadingPreventionHandler<>();
    private final OverLoadingPreventionHandler<ShipmentResponse> shipmentHandler = new OverLoadingPreventionHandler<>();
    private final OverLoadingPreventionHandler<TrackResponse> trackHandler = new OverLoadingPreventionHandler<>();

    @Test
    void testOverLoadingPreventionHandler() {
        pricingHandler.getBulkCallsOrSuspend(pricingGateway::getPricing)

    }

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = this.port;

        when(pricingGateway.getPricing(any())).thenReturn(createError());
        when(trackingGateway.getTracking(any())).thenReturn(createError());
        when(shipmentGateway.getShipment(any())).thenReturn(createError());
    }

}
*/
