package com.fedex.aggregation.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

public class WebClientFactory {

/*     Configurations can potentially be added per specific API,
    therefore WebClient static factory method is kept in WebClientFactory*/

    public static WebClient createWebClient(String baseUrl) {
        return WebClient
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(new ObjectMapper(), MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new ObjectMapper(), MediaType.APPLICATION_JSON));

                })
                .defaultHeaders(httpHeaders -> accept(MediaType.APPLICATION_JSON))
                .baseUrl(baseUrl)
                .build();
    }
}
