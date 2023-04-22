package com.fedex.aggregation.service.gateway;

import com.fedex.aggregation.service.model.PricingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.fedex.aggregation.service.util.StringUtil.getStringSet;

@Component
public class PricingClient extends BulkRequestHandler<PricingResponse> implements PricingGateway {
    private static final Logger logger = LoggerFactory.getLogger(PricingClient.class);
    private final WebClient client;
    private ConcurrentLinkedQueue<PricingResponse> callbackQueue;
    private final Flux<PricingResponse> flux;
    public static final PricingResponse defaultPricingResponse = new PricingResponse(null);

    public PricingClient(@Qualifier("pricingWebClient") WebClient client,
                         @Autowired Sinks.Many<PricingResponse> pricingSink,
                         @Autowired Flux<PricingResponse> flux,
                         @Autowired ConcurrentLinkedQueue<PricingResponse> callbackQueue
    ) {
        super(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), pricingSink);
        this.client = client;
        this.flux = flux;
        this.callbackQueue = callbackQueue;
    }

    @Override
    public Flux<PricingResponse> getPricing(String countryCodes) {
        getBulkCallsOrWait(this::get, getStringSet(countryCodes));
        return flux.doOnComplete(() -> {
                    logger.info("COMPLETED!");
                    getSink().emitComplete((signalType, emitResult) -> emitResult.isSuccess());
                })
                .doOnNext(pricingResponse -> {
/*
                            callbackQueue.add(pricingResponse);
*/
                            logger.info("This is the subscribed PricingResponse:{}", pricingResponse);
                        }
                );
    }

    @Override
    public Mono<PricingResponse> get(String countryCodes) {
        logger.info("Calling Pricing API with following countryCodes={}", countryCodes);
        return (!countryCodes.isBlank()
                ?
                client
                        .get()
                        .uri(builder ->
                                builder.path("/pricing").queryParam("q", countryCodes).build()
                        )
                        .retrieve()
                        .bodyToMono(PricingResponse.class)
                        .onErrorReturn(defaultPricingResponse)
/*
                        .log()
*/
                : Mono.empty());
    }
}

