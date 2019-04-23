package com.github.carlosraphael.fx.rate.service;

import com.github.carlosraphael.fx.rate.domain.FxRate;
import reactor.core.publisher.Mono;

import java.util.Currency;

public interface FxRateService {

    Mono<FxRate> getRate(Currency source, Currency target);
}
