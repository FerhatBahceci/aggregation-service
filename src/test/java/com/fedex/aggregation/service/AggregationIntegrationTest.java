package com.fedex.aggregation.service;

import com.fedex.aggregation.service.model.AggregatedResponse;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.DockerComposeContainer;
import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import static com.fedex.aggregation.service.config.WebClientFactory.createWebClient;
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

    @After
    void after() {
        environment.stop();
        environment.withRemoveImages(DockerComposeContainer.RemoveImages.ALL);
    }

    @Test
    void testAggregation() {

        var pricing = "NL,CN";
        var track = "109347263,123456891";
        var shipments = "109347263,123456891";
        Set<String> pricingSet = Arrays.stream(pricing.split(",")).collect(Collectors.toSet());
        Set<Long> trackInLong = Arrays.stream(track.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        Set<Long> shipmentsInLong = Arrays.stream(shipments.split(",")).map(Long::valueOf).collect(Collectors.toSet());

        var uri = String.format("http://localhost:%s/aggregation?pricing=%s&track=%s&shipments=%s", aggregatorServicePort, pricing, track, shipments);
        var response = getCall(uri, AggregatedResponse.class);

        assertThat(response).isNotNull();
        if (nonNull(response.pricing())) assertThat(response.pricing().keySet()).containsAll(pricingSet);
        if (nonNull(response.track())) assertThat(response.track().keySet()).containsAll(trackInLong);
        if (nonNull(response.shipments())) assertThat(response.shipments().keySet()).containsAll(shipmentsInLong);
    }

    @Test
    void testAggregationEmptyRequest() {

        var uri = String.format("http://localhost:%s/aggregation", aggregatorServicePort);
        var response = getCall(uri, AggregatedResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.pricing()).isNull();
        assertThat(response.track()).isNull();
        assertThat(response.shipments()).isNull();
    }

    @Test
    void testAggregationOnlyPricingRequestParams() {

        var pricing = "NL,CN";
        Set<String> pricingSet = Arrays.stream(pricing.split(",")).collect(Collectors.toSet());

        var uri = String.format("http://localhost:%s/aggregation?pricing=%s", aggregatorServicePort, pricing);
        var response = getCall(uri, AggregatedResponse.class);

        assertThat(response).isNotNull();
        if (nonNull(response.pricing())) assertThat(response.pricing().keySet()).containsAll(pricingSet);
        assertThat(response.track()).isNull();
        assertThat(response.shipments()).isNull();
    }

    @Test
    void testAggregationOnlyTrackRequestParams() {

        var track = "109347263,123456891";
        Set<Long> trackInLong = Arrays.stream(track.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        var uri = String.format("http://localhost:%s/aggregation?track=%s", aggregatorServicePort, track);

        var response = getCall(uri, AggregatedResponse.class);

        assertThat(response).isNotNull();
        if (nonNull(response.track())) assertThat(response.track().keySet()).containsAll(trackInLong);
        assertThat(response.pricing()).isNull();
        assertThat(response.shipments()).isNull();
    }


    @Test
    void testAggregationOnlyShipmentsRequestParams() {
        var shipments = "109347263,123456891";
        Set<Long> shipmentsInLong = Arrays.stream(shipments.split(",")).map(Long::valueOf).collect(Collectors.toSet());

        var uri = String.format("http://localhost:%s/aggregation?shipments=%s", aggregatorServicePort, shipments);
        var response = getCall(uri, AggregatedResponse.class);

        assertThat(response).isNotNull();
        if (nonNull(response.shipments())) assertThat(response.shipments().keySet()).containsAll(shipmentsInLong);
        assertThat(response.pricing()).isNull();
        assertThat(response.track()).isNull();
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
