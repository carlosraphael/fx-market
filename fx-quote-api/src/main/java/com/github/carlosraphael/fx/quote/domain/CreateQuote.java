package com.github.carlosraphael.fx.quote.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

@Value @Builder
public class CreateQuote {

    private final Currency source;
    private final Currency target;
    private final BigDecimal sourceAmount;
}
