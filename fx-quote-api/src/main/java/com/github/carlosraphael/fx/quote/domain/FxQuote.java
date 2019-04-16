package com.github.carlosraphael.fx.quote.domain;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;

@Table
@Value @Builder
public class FxQuote {

    @PrimaryKey
    private final String id;
    private final Currency source;
    private final Currency target;
    private final BigDecimal sourceAmount;
    private final BigDecimal targetAmount;
    private final BigDecimal rate;
    private final BigDecimal fee;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime expireAt;

}
