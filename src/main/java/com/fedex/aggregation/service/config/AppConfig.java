package com.fedex.aggregation.service.config;

import com.fedex.aggregation.service.model.PricingResponse;
import com.fedex.aggregation.service.model.ShipmentResponse;
import com.fedex.aggregation.service.model.TrackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentLinkedQueue;


@Configuration
public class AppConfig {

    //Queue
    @Bean
    public ConcurrentLinkedQueue<TrackResponse> trackQueue() {
        return new ConcurrentLinkedQueue<>();
    }

    @Bean
    public ConcurrentLinkedQueue<ShipmentResponse> shipmentsQueue() {
        return new ConcurrentLinkedQueue<>();
    }

    @Bean
    public ConcurrentLinkedQueue<PricingResponse> pricingQueue() {
        return new ConcurrentLinkedQueue<>();
    }

    // Sinks
    @Bean
    public Sinks.Many<PricingResponse> pricingSink(@Autowired ConcurrentLinkedQueue<PricingResponse> queue) {
        return Sinks.unsafe().many().unicast().onBackpressureBuffer(queue);
    }

    @Bean
    public Flux<PricingResponse> pricingFlux(Sinks.Many<PricingResponse> sink) {
        return sink.asFlux();
    }

    @Bean
    public Sinks.Many<ShipmentResponse> shipmentSink(@Autowired ConcurrentLinkedQueue<ShipmentResponse> queue) {
        return Sinks.unsafe().many().unicast().onBackpressureBuffer(queue);
    }

    @Bean
    public Flux<ShipmentResponse> shipmentFlux(Sinks.Many<ShipmentResponse> sink) {
        return sink.asFlux();
    }

    @Bean
    public Sinks.Many<TrackResponse> trackSink(@Autowired ConcurrentLinkedQueue<TrackResponse> queue) {
        return Sinks.unsafe().many().unicast().onBackpressureBuffer(queue);
    }

    @Bean
    public Flux<TrackResponse> trackFlux(Sinks.Many<TrackResponse> sink) {
        return sink.asFlux();
    }
}
