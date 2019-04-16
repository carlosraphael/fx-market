package com.github.carlosraphael.fx.quote.service.impl;

import com.github.carlosraphael.fx.quote.domain.CreateQuote;
import com.github.carlosraphael.fx.quote.domain.FxQuote;
import com.github.carlosraphael.fx.quote.domain.Rate;
import com.github.carlosraphael.fx.quote.repository.FxQuoteRepository;
import com.github.carlosraphael.fx.quote.service.FxQuoteService;
import com.github.carlosraphael.fx.quote.service.RateService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FxQuoteServiceImpl implements FxQuoteService {

    private static final int QUOTE_VALIDITY_IN_MINUTES = 30;

    private final RateService rateService;
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
    public Mono<FxQuote> create(CreateQuote newQuote) {
        return rateService.getRate(newQuote.getSource(), newQuote.getTarget())
                .map(rate -> Tuple.of(rate, newQuote))
                .flatMap(this::createQuote);

    }

    private Mono<FxQuote> createQuote(Tuple2<Rate, CreateQuote> tuple) {
        Rate rate = tuple._1;
        CreateQuote newQuote = tuple._2;
        OffsetDateTime createdAt = OffsetDateTime.now();

        return fxQuoteRepository.save(
                FxQuote.builder()
                        .id(UUID.randomUUID().toString())
                        .source(newQuote.getSource())
                        .target(newQuote.getTarget())
                        .sourceAmount(newQuote.getSourceAmount())
                        .targetAmount(newQuote.getSourceAmount().multiply(rate.getRate()))
                        .rate(rate.getRate())
                        .fee(BigDecimal.ZERO) // TODO
                        .createdAt(createdAt)
                        .expireAt(createdAt.plusMinutes(QUOTE_VALIDITY_IN_MINUTES))
                        .build()
        );
    }
}
