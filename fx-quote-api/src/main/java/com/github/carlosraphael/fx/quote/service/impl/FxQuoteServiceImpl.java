package com.github.carlosraphael.fx.quote.service.impl;

import com.github.carlosraphael.fx.quote.domain.FxQuote;
import com.github.carlosraphael.fx.quote.repository.FxQuoteRepository;
import com.github.carlosraphael.fx.quote.service.FxQuoteService;
import com.github.carlosraphael.fx.quote.service.FxRateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class FxQuoteServiceImpl implements FxQuoteService {

    private final FxRateService fxRateService;
    private final FxQuoteRepository fxQuoteRepository;

    @Override
    public Mono<FxQuote> getQuoteById(String id) {
        return fxQuoteRepository.findById(id);
    }

    @Override
    public Flux<FxQuote> getQuotes() {
        return fxQuoteRepository.findAll();
    }

    @Override
    public Mono<FxQuote> create(FxQuote newQuote) {
        return fxRateService.getRate(newQuote.getSource(), newQuote.getTarget())
                .flatMap(fxRate -> fxQuoteRepository.save(newQuote.basedOn(fxRate)));
    }
}
