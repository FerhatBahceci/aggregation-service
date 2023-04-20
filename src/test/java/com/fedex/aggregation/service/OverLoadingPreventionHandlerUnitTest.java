package com.fedex.aggregation.service;

import com.fedex.aggregation.service.gateway.BulkRequestHandler;
import com.fedex.aggregation.service.gateway.PricingGateway;
import com.fedex.aggregation.service.gateway.ShipmentGateway;
import com.fedex.aggregation.service.gateway.TrackGateway;
import com.fedex.aggregation.service.model.PricingResponse;
import com.fedex.aggregation.service.model.ShipmentResponse;
import com.fedex.aggregation.service.model.TrackResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fedex.aggregation.service.TestData.*;
import static com.fedex.aggregation.service.gateway.BulkRequestHandler.cap;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class OverLoadingPreventionHandlerUnitTest {
    @LocalServerPort
    Integer port = 0;
    @Autowired
    private Sinks.Many<PricingResponse> pricingSink;

    @Autowired
    private Flux<PricingResponse> pricingFlux;

    @MockBean
    private PricingGateway pricingGatewayMock;

    @Autowired
    private Sinks.Many<TrackResponse> trackSink;

    @Autowired
    private Flux<TrackResponse> trackFlux;

    @MockBean
    private TrackGateway trackGatewayMock;

    @Autowired
    private Sinks.Many<ShipmentResponse> shipmentSink;

    @Autowired
    private Flux<ShipmentResponse> shipmentFlux;

    @MockBean
    private ShipmentGateway shipmentGatewayMock;

    private final BulkRequestHandler<TrackResponse> trackBulkRequestHandler = new BulkRequestHandler<>();

    private final BulkRequestHandler<ShipmentResponse> shipmentBulkRequestHandler = new BulkRequestHandler<>();

    @Test
    void testPricingOverLoadingPreventionHandler() {
        when(pricingGatewayMock.getPricing(any())).thenReturn(PRICING_RESPONSE);
        BulkRequestHandler<PricingResponse> handler = new BulkRequestHandler<>();
        var orderIdsBelowCap = Set.of(ORDER_ID_1, ORDER_ID_2, ORDER_ID_3, ORDER_ID_4).stream().map(Objects::toString).collect(Collectors.toSet());  // queryParam below cap!


        handler.getBulkCallsOrWait(pricingGatewayMock::getPricing, orderIdsBelowCap, pricingSink);  // 1.

        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo(orderIdsBelowCap.size());
        Assertions.assertThat(handler.getCallbackQueue()).isEmpty();
        verify((pricingGatewayMock), times(0)).getPricing(any()); // Should not fetch pricing since below cap

        handler.getBulkCallsOrWait(pricingGatewayMock::getPricing, orderIdsBelowCap, pricingSink); // 2.

        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo((orderIdsBelowCap.size() * 2) - cap);
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(1);  // 1 call has been stored in the callbackQueue
        verify((pricingGatewayMock), times(1)).getPricing(any()); // Should fetch pricing since cap is reached!

        handler.getBulkCallsOrWait(pricingGatewayMock::getPricing, orderIdsBelowCap, pricingSink); // 3.
        handler.getBulkCallsOrWait(pricingGatewayMock::getPricing, orderIdsBelowCap, pricingSink); // 4.
        handler.getBulkCallsOrWait(pricingGatewayMock::getPricing, orderIdsBelowCap, pricingSink); // 5.
        handler.getBulkCallsOrWait(pricingGatewayMock::getPricing, orderIdsBelowCap, pricingSink); // 6. ---> 6 x 4 (queryParams) = 24, therefore 4 API calls should be residing in the queue
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(4);

        handler.getBulkCallsOrWait(pricingGatewayMock::getPricing, orderIdsBelowCap, pricingSink); // 7. ---> 7 x 4(queryParams) = 28, 5 queryParam per API call --> cap is reached and executed!
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(0);
        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo(3);
    }
}