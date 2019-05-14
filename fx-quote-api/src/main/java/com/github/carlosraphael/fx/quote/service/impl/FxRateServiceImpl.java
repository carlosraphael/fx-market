package com.github.carlosraphael.fx.quote.service.impl;

import com.github.carlosraphael.fx.quote.domain.FxRate;
import com.github.carlosraphael.fx.quote.service.FxRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Currency;

import static com.google.common.collect.ImmutableMap.of;

@Service
@RequiredArgsConstructor
public class FxRateServiceImpl implements FxRateService {

    private final WebClient webClient;

    @Override
    public Mono<FxRate> getRate(Currency source, Currency target) {
        return webClient.get()
                .uri("/rates", of("source", source.getCurrencyCode(), "target", target.getCurrencyCode()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FxRate.class);
    }
}
