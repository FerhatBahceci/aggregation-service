package com.fedex.aggregation.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/* Shipments, tracking and pricing API would/should probably have their own services running on separate images IF this would be a more "realistic" scenario aligned with microservice based architecture (shipment-service, tracking-service, pricing-service)
 Therefore, a more realistic approach would be one bean instance of WebClient per respective API"*/
@Configuration
public class WebClientConfig {

    @Bean("shipmentWebClient")
    public static WebClient shipmentsWebClient(@Value("${provider-api.shipment}") String shipmentsUrl) {
        return WebClientFactory.createWebClient(shipmentsUrl);
    }

    @Bean("trackingWebClient")
    public static WebClient trackingWebClient(@Value("${provider-api.tracking}") String trackingUrl) {
        return WebClientFactory.createWebClient(trackingUrl);
    }

    @Bean("pricingWebClient")
    public static WebClient pricingWebClient(@Value("${provider-api.pricing}") String pricingUrl) {
        return WebClientFactory.createWebClient(pricingUrl);
    }
}
