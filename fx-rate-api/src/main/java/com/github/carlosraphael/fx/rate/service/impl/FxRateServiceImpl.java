package com.github.carlosraphael.fx.rate.service.impl;

import com.github.carlosraphael.fx.rate.domain.FxRate;
import com.github.carlosraphael.fx.rate.service.FxRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FxRateServiceImpl implements FxRateService {

    static final String CACHE_NAME = "rates";

    private final WebClient webClient;
    private final CacheManager cacheManager;

    @Value("${rate.api.URI}")
    private UriTemplate uriTemplate;

    @Override
    public Mono<FxRate> getRate(Currency source, Currency target) {
        String currencyQuery = source.getCurrencyCode() + "_" + target.getCurrencyCode();

        return CacheMono.lookup(key -> Mono.justOrEmpty(getCachedRate(key)).map(Signal::next), currencyQuery)
                .onCacheMissResume(() -> fetchRemoteRateApi(source, target, currencyQuery))
                .andWriteWith((key, signal) -> Mono.fromRunnable(() -> getCache().put(key, signal.get())));
    }

    private Mono<FxRate> fetchRemoteRateApi(Currency source, Currency target, String currencyQuery) {
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
                );
    }

    private FxRate getCachedRate(String key) {
        return (FxRate) getCache().get(key);
    }

    private Map<Object, Object> getCache() {
        return ((CaffeineCache) Objects.requireNonNull(cacheManager.getCache(CACHE_NAME))).getNativeCache().asMap();
    }
}
