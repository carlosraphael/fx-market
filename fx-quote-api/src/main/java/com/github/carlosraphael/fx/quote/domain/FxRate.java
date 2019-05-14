package com.github.carlosraphael.fx.quote.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;

@Value @Builder
public class FxRate {

    private final BigDecimal rate;
    private final Currency source;
    private final Currency target;
    private final OffsetDateTime time;
}
