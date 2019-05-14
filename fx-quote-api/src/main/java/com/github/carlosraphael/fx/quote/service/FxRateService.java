package com.github.carlosraphael.fx.quote.service;

import com.github.carlosraphael.fx.quote.domain.FxRate;
import reactor.core.publisher.Mono;

import java.util.Currency;

public interface FxRateService {

    Mono<FxRate> getRate(Currency source, Currency target);
}
