package com.fedex.aggregation.service;


import com.fedex.aggregation.service.model.AggregatedResponse;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.DockerComposeContainer;
import java.io.File;

import static com.fedex.aggregation.service.config.WebClientFactory.createWebClient;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AggregationIntegrationTest {

    private static final String serviceName = "fedex_1";

    private static final int servicePort = 8080;

    private final WebClient webClient = createWebClient("http://localhost:8081");
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

        webClient
                .get()
                .uri("http://localhost:8081/aggregation?pricing=NL,CN&track=109347263,123456891&shipments=109347263,123456891")
                .retrieve()
                .bodyToMono(AggregatedResponse.class);

    }
}
