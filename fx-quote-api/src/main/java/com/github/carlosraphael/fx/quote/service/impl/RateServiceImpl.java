package com.github.carlosraphael.fx.quote.service.impl;

import com.github.carlosraphael.fx.quote.domain.Rate;
import com.github.carlosraphael.fx.quote.service.RateService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Currency;

import static com.google.common.collect.ImmutableMap.of;

@Service
public class RateServiceImpl implements RateService {

    private final WebClient webClient;

    // TODO: setup api token access and base URL
    public RateServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Rate> getRate(Currency source, Currency target) {
        return webClient.get()
                .uri("/rates", of("source", source.getCurrencyCode(), "target", target.getCurrencyCode()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(Rate.class);
    }
}
