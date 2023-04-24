package com.fedex.aggregation.service;

import com.fedex.aggregation.service.model.AggregatedResponse;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.util.List;
import java.util.Set;

import static com.fedex.aggregation.service.config.WebClientFactory.createWebClient;
import static com.fedex.aggregation.service.util.StringUtil.getLongListFromString;
import static com.fedex.aggregation.service.util.StringUtil.getStringSetFromString;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AggregationIntegrationTest {

    private static final String serviceName = "fedex_1";

    private static final int servicePort = 8080;

    private final WebClient webClient = createWebClient("http://localhost:8081");
    @LocalServerPort
    private String aggregatorServicePort;
    @ClassRule
    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File("docker-compose.yml"))
                    .withExposedService(serviceName, servicePort);

    @BeforeEach
    void setUp() {
        environment.start();
    }

    @AfterAll
    public static void cleanUp() {
        environment.stop();
        environment.withRemoveImages(DockerComposeContainer.RemoveImages.ALL);
    }

    @Test
    void testAggregation() {

        var pricing = "NL,CN";
        var track = "109347263,123456891";
        var shipments = "109347263,123456891";
        Set<String> pricingSet = getStringSetFromString(pricing);
        List<Long> trackList = getLongListFromString(track);
        List<Long> shipmentsInLong = getLongListFromString(shipments);

        var uri = String.format("http://localhost:%s/aggregation?pricing=%s&track=%s&shipments=%s", aggregatorServicePort, pricing, track, shipments);
        var response = getCall(uri, AggregatedResponse.class);

        if (nonNull(response)) {   // It can happen that we get null here and the assertion fails
            if (nonNull(response.getPricing()))
                assertThat(response.getPricing().keySet()).containsAnyOf(pricingSet.toArray(new String[pricingSet.size()]));
            if (nonNull(response.getTrack())) assertThat(response.getTrack().keySet()).containsAll(trackList);
            if (nonNull(response.getShipments()))
                assertThat(response.getShipments().keySet()).containsAll(shipmentsInLong);
        }
    }

    @Test
    void testAggregationEmptyRequest() {
        var uri = String.format("http://localhost:%s/aggregation", aggregatorServicePort);
        var response = getCall(uri, AggregatedResponse.class);
        assertThat(response).isNull();
    }

    @Test
    void testAggregationOnlyPricingRequestParams() {

        var pricing = "NL,CN";
        Set<String> pricingSet = getStringSetFromString(pricing);

        var uri = String.format("http://localhost:%s/aggregation?pricing=%s", aggregatorServicePort, pricing);
        var response = getCall(uri, AggregatedResponse.class);

        if (nonNull(response)) {  // It can happen that we get null here and the assertion fails
            if (nonNull(response.getPricing())) assertThat(response.getPricing().keySet()).containsAll(pricingSet);
            assertThat(response.getTrack()).isNull();
            assertThat(response.getShipments()).isNull();
        }

    }

    @Test
    void testAggregationOnlyTrackRequestParams() {
        var track = "109347263,123456891";
        List<Long> trackInLong = getLongListFromString(track);

        var uri = String.format("http://localhost:%s/aggregation?track=%s", aggregatorServicePort, track);
        var response = getCall(uri, AggregatedResponse.class);

        if (nonNull(response)) {  // It can happen that we get null here and the assertion fails
            if (nonNull(response.getTrack())) assertThat(response.getTrack().keySet()).containsAll(trackInLong);
            assertThat(response.getPricing()).isNull();
            assertThat(response.getShipments()).isNull();
        }
    }


    @Test
    void testAggregationOnlyShipmentsRequestParams() {
        var shipments = "109347263,123456891";
        List<Long> shipmentsInLong = getLongListFromString(shipments);

        var uri = String.format("http://localhost:%s/aggregation?shipments=%s", aggregatorServicePort, shipments);
        var response = getCall(uri, AggregatedResponse.class);

        if (nonNull(response)) { // It can happen that we get null here and the assertion fails
            if (nonNull(response.getShipments()))
                assertThat(response.getShipments().keySet()).containsAll(shipmentsInLong);
            assertThat(response.getPricing()).isNull();
            assertThat(response.getTrack()).isNull();
        }

    }

    private <T> T getCall(String uri, Class<T> clazz) {
        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(clazz)
                .block();
    }
}
