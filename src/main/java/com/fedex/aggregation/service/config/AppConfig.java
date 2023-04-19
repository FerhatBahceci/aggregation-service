package com.fedex.aggregation.service.config;

import com.fedex.aggregation.service.model.PricingResponse;
import com.fedex.aggregation.service.model.ShipmentResponse;
import com.fedex.aggregation.service.model.TrackResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class AppConfig {

    @Bean
    public Sinks.Many<PricingResponse> pricingSink() {
        return Sinks.many().replay().latest();
    }

    @Bean
    public Flux<PricingResponse> pricingFlux(Sinks.Many<PricingResponse> sink) {
        return sink.asFlux().cache();
    }

    @Bean
    public Sinks.Many<ShipmentResponse> shipmentSink() {
        return Sinks.many().replay().latest();
    }

    @Bean
    public Flux<ShipmentResponse> shipmentFlux(Sinks.Many<ShipmentResponse> sink) {
        return sink.asFlux().cache();
    }

    @Bean
    public Sinks.Many<TrackResponse> trackSink() {
        return Sinks.many().replay().latest();
    }

    @Bean
    public Flux<TrackResponse> trackFlux(Sinks.Many<TrackResponse> sink) {
        return sink.asFlux().cache();
    }
}