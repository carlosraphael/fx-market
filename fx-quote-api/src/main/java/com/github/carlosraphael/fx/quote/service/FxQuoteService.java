package com.github.carlosraphael.fx.quote.service;

import com.github.carlosraphael.fx.quote.domain.FxQuote;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FxQuoteService {

    Mono<FxQuote> getQuoteById(String id);
    Flux<FxQuote> getQuotes();
    Mono<FxQuote> create(FxQuote newQuote);
}
