package com.github.carlosraphael.fx.quote.service;

import com.github.carlosraphael.fx.quote.domain.Rate;
import reactor.core.publisher.Mono;

import java.util.Currency;

public interface RateService {

    Mono<Rate> getRate(Currency source, Currency target);
}
