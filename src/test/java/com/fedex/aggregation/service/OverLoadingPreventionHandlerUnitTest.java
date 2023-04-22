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
import reactor.core.publisher.Sinks;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static com.fedex.aggregation.service.TestData.*;
import static com.fedex.aggregation.service.gateway.BulkRequestHandler.cap;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class OverLoadingPreventionHandlerUnitTest {
    @Autowired
    private Sinks.Many<PricingResponse> pricingSink;

    @Autowired
    private Sinks.Many<TrackResponse> trackSink;

    @Autowired
    private Sinks.Many<ShipmentResponse> shipmentSink;

    @MockBean
    private PricingGateway pricingGatewayMock;

    @MockBean
    private TrackGateway trackGatewayMock;

    @MockBean
    private ShipmentGateway shipmentGatewayMock;

    @Test
    void testPricingOverLoadingPreventionHandler() {

        when(pricingGatewayMock.getPricing(any())).thenReturn(PRICING_RESPONSE);
        BulkRequestHandler<PricingResponse> handler = new BulkRequestHandler<>(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), pricingSink);
        var orderIdsBelowCap = Set.of(ORDER_ID_1, ORDER_ID_2, ORDER_ID_3, ORDER_ID_4).stream().map(Objects::toString).collect(Collectors.toSet());  // queryParam below cap!

        handler.getBulkCallsOrWait(pricingGatewayMock::get, orderIdsBelowCap);  // 1.

        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo(orderIdsBelowCap.size());
        Assertions.assertThat(handler.getCallbackQueue()).isEmpty();
        verify((pricingGatewayMock), times(0)).getPricing(any()); // Should not fetch pricing since below cap

        handler.getBulkCallsOrWait(pricingGatewayMock::get, orderIdsBelowCap); // 2.

        verify((pricingGatewayMock), times(1)).getPricing(any()); // Should not fetch pricing since below cap
        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo((orderIdsBelowCap.size() * 2) - cap);
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(0);
    }

    @Test
    void testTrackingOverLoadingPreventionHandler() {

        when(trackGatewayMock.getTracking(any())).thenReturn(TRACK_RESPONSE);
        BulkRequestHandler<TrackResponse> handler = new BulkRequestHandler<>(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), trackSink);
        var orderIdsBelowCap = Set.of(ORDER_ID_1, ORDER_ID_2, ORDER_ID_3, ORDER_ID_4).stream().map(Objects::toString).collect(Collectors.toSet());  // queryParam below cap!

        handler.getBulkCallsOrWait(trackGatewayMock::get, orderIdsBelowCap);  // 1.

        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo(orderIdsBelowCap.size());
        Assertions.assertThat(handler.getCallbackQueue()).isEmpty();
        verify((trackGatewayMock), times(0)).getTracking(any()); // Should not fetch pricing since below cap

        handler.getBulkCallsOrWait(trackGatewayMock::get, orderIdsBelowCap); // 2.

        verify((trackGatewayMock), times(1)).getTracking(any()); // Should not fetch pricing since below cap
        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo((orderIdsBelowCap.size() * 2) - cap);
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(0);
    }

    @Test
    void testShipmentOverLoadingPreventionHandler() {

        when(shipmentGatewayMock.getShipment(any())).thenReturn(SHIPMENT_RESPONSE);
        BulkRequestHandler<ShipmentResponse> handler = new BulkRequestHandler<>(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), shipmentSink);
        var orderIdsBelowCap = Set.of(ORDER_ID_1, ORDER_ID_2, ORDER_ID_3, ORDER_ID_4).stream().map(Objects::toString).collect(Collectors.toSet());  // queryParam below cap!

        handler.getBulkCallsOrWait(shipmentGatewayMock::get, orderIdsBelowCap);  // 1.

        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo(orderIdsBelowCap.size());
        Assertions.assertThat(handler.getCallbackQueue()).isEmpty();
        verify((shipmentGatewayMock), times(0)).getShipment(any()); // Should not fetch pricing since below cap

        handler.getBulkCallsOrWait(shipmentGatewayMock::get, orderIdsBelowCap); // 2.

        verify((shipmentGatewayMock), times(1)).getShipment(any()); // Should not fetch pricing since below cap
        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo((orderIdsBelowCap.size() * 2) - cap);
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(0);
    }
}
