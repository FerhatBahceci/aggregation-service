/*
package com.fedex.aggregation.service;

import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackGateway;
import com.fedex.aggregation.service.model.PricingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import reactor.core.publisher.Flux;
import java.util.Set;
import static com.fedex.aggregation.service.TestData.PRICING_RESPONSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class OverLoadingPreventionHandlerUnitTest {

    @LocalServerPort
    Integer port = 0;
    @MockBean
    PricingGateway pricingGateway;

    @MockBean
    TrackGateway trackingGateway;

    @MockBean
    ShipmentGateway shipmentGateway;

    @Test
    void testOverLoadingPreventionHandler() {

        when(pricingGateway.getPricing(any())).thenReturn(PRICING_RESPONSE);

        Flux<PricingResponse> response = pricingHandler.getBulkCallsOrSuspend(pricingGateway::getPricing, Set.of("SE,NL"));


    }
}
*/
