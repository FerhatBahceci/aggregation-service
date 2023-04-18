package com.fedex.aggregation.service;

import com.fedex.aggregation.service.model.AggregatedResponse;
import com.fedex.aggregation.service.model.TrackResponse;
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

    @Test
    void testAggregation() {

        var pricing = "NL,CN";
        var track = "109347263,123456891";
        var shipments = "109347263,123456891";
        Set<String> pricingSet = Arrays.stream(pricing.split(",")).collect(Collectors.toSet());
        Set<Long> trackInLong = Arrays.stream(track.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        Set<Long> shipmentsInLong = Arrays.stream(shipments.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        var uri = String.format("http://localhost:%s/aggregation?pricing=%s&track=%s&shipments=%s", aggregatorServicePort, pricing, track, shipments);

        var response = webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(AggregatedResponse.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.pricing().keySet()).containsAll(pricingSet);
        assertThat(response.track().keySet()).containsAll(trackInLong);
        assertThat(response.track().values()).containsAnyOf(TrackResponse.Status.values());
        assertThat(response.shipments().keySet()).containsAll(shipmentsInLong);
    }
}
