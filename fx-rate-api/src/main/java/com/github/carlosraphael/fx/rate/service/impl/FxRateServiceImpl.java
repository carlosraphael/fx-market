package com.github.carlosraphael.fx.rate.service.impl;

import com.github.carlosraphael.fx.rate.domain.FxRate;
import com.github.carlosraphael.fx.rate.service.FxRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FxRateServiceImpl implements FxRateService {

    private final WebClient webClient;

    @Value("${fxRate.api.URI}")
    private UriTemplate uriTemplate;

    @Override
    @Cacheable(cacheNames = "rates", key = "{#source.currencyCode, #target.currencyCode}")
    public Mono<FxRate> getRate(Currency source, Currency target) {
        String currencyQuery = source.getCurrencyCode() + "_" + target.getCurrencyCode();

        return webClient.get().uri(uriTemplate.expand(currencyQuery).toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .map(rate -> FxRate.builder()
                        .source(source)
                        .target(target)
                        .rate(new BigDecimal(rate.get(currencyQuery).toString()))
                        .time(OffsetDateTime.now())
                        .build()
                ).cache();
    }
}
