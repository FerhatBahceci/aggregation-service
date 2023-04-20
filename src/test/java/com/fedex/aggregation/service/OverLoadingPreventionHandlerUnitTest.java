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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

/*
        pricingFlux.map(pricingResponse -> Assertions.assertThat(pricingResponse).isEqualTo(PRICING_RESPONSE.block())).blockFirst();
*/
    }

    @Test
    void testTrackingOverLoadingPreventionHandler() {
        when(trackGatewayMock.getTracking(any())).thenReturn(TRACK_RESPONSE);
        BulkRequestHandler<TrackResponse> handler = new BulkRequestHandler<>();
        var orderIdsBelowCap = Set.of(ORDER_ID_1, ORDER_ID_2, ORDER_ID_3, ORDER_ID_4).stream().map(Objects::toString).collect(Collectors.toSet());  // queryParam below cap!

        handler.getBulkCallsOrWait(trackGatewayMock::getTracking, orderIdsBelowCap, trackSink);  // 1.

        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo(orderIdsBelowCap.size());
        Assertions.assertThat(handler.getCallbackQueue()).isEmpty();
        verify((trackGatewayMock), times(0)).getTracking(any()); // Should not fetch pricing since below cap

        handler.getBulkCallsOrWait(trackGatewayMock::getTracking, orderIdsBelowCap, trackSink); // 2.

        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo((orderIdsBelowCap.size() * 2) - cap);
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(1);  // 1 call has been stored in the callbackQueue
        verify((trackGatewayMock), times(1)).getTracking(any()); // Should fetch pricing since cap is reached!

        handler.getBulkCallsOrWait(trackGatewayMock::getTracking, orderIdsBelowCap, trackSink); // 3.
        handler.getBulkCallsOrWait(trackGatewayMock::getTracking, orderIdsBelowCap, trackSink); // 4.
        handler.getBulkCallsOrWait(trackGatewayMock::getTracking, orderIdsBelowCap, trackSink); // 5.
        handler.getBulkCallsOrWait(trackGatewayMock::getTracking, orderIdsBelowCap, trackSink); // 6. ---> 6 x 4 (queryParams) = 24, therefore 4 API calls should be residing in the queue
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(4);

        handler.getBulkCallsOrWait(trackGatewayMock::getTracking, orderIdsBelowCap, trackSink); // 7. ---> 7 x 4(queryParams) = 28, 5 queryParam per API call --> cap is reached and executed!
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(0);
        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo(3);

/*
        trackFlux.map(trackResponse -> Assertions.assertThat(trackResponse).isEqualTo(TRACK_RESPONSE.block())).blockFirst();
*/

    }

    @Test
    void testShipmentOverLoadingPreventionHandler() {
        when(shipmentGatewayMock.getShipment(any())).thenReturn(SHIPMENT_RESPONSE);
        BulkRequestHandler<ShipmentResponse> handler = new BulkRequestHandler<>();
        var orderIdsBelowCap = Set.of(ORDER_ID_1, ORDER_ID_2, ORDER_ID_3, ORDER_ID_4).stream().map(Objects::toString).collect(Collectors.toSet());  // queryParam below cap!

        handler.getBulkCallsOrWait(shipmentGatewayMock::getShipment, orderIdsBelowCap, shipmentSink);  // 1.

        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo(orderIdsBelowCap.size());
        Assertions.assertThat(handler.getCallbackQueue()).isEmpty();
        verify((shipmentGatewayMock), times(0)).getShipment(any()); // Should not fetch pricing since below cap

        handler.getBulkCallsOrWait(shipmentGatewayMock::getShipment, orderIdsBelowCap, shipmentSink); // 2.

        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo((orderIdsBelowCap.size() * 2) - cap);
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(1);  // 1 call has been stored in the callbackQueue
        verify((shipmentGatewayMock), times(1)).getShipment(any()); // Should fetch pricing since cap is reached!

        handler.getBulkCallsOrWait(shipmentGatewayMock::getShipment, orderIdsBelowCap, shipmentSink); // 3.
        handler.getBulkCallsOrWait(shipmentGatewayMock::getShipment, orderIdsBelowCap, shipmentSink); // 4.
        handler.getBulkCallsOrWait(shipmentGatewayMock::getShipment, orderIdsBelowCap, shipmentSink);
        ; // 5.
        handler.getBulkCallsOrWait(shipmentGatewayMock::getShipment, orderIdsBelowCap, shipmentSink);
        ; // 6. ---> 6 x 4 (queryParams) = 24, therefore 4 API calls should be residing in the queue
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(4);

        handler.getBulkCallsOrWait(shipmentGatewayMock::getShipment, orderIdsBelowCap, shipmentSink); // 7. ---> 7 x 4(queryParams) = 28, 5 queryParam per API call --> cap is reached and executed!
        Assertions.assertThat(handler.getCallbackQueue().size()).isEqualTo(0);
        Assertions.assertThat(handler.getQueryParamsQueue().size()).isEqualTo(3);

/*
        shipmentFlux.map(shipmentResponse -> Assertions.assertThat(shipmentResponse).isEqualTo(SHIPMENT_RESPONSE.block())).blockFirst();
*/
    }
}