package com.github.carlosraphael.fx.quote.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.UUID;

@Table
@Value @Builder
@EqualsAndHashCode(of = "id")
public class FxQuote {

    private static final int QUOTE_VALIDITY_IN_MINUTES = 30;

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

    public FxQuote basedOn(FxRate fxRate) {
        OffsetDateTime createdAt = OffsetDateTime.now();
        OffsetDateTime expireAt = createdAt.plusMinutes(QUOTE_VALIDITY_IN_MINUTES);
        BigDecimal targetAmount = this.getSourceAmount().multiply(fxRate.getRate());

        return FxQuote.builder()
                .id(UUID.randomUUID().toString())
                .source(this.getSource())
                .target(this.getTarget())
                .sourceAmount(this.getSourceAmount())
                .targetAmount(targetAmount)
                .rate(fxRate.getRate())
                .fee(BigDecimal.ZERO) // TODO
                .createdAt(createdAt)
                .expireAt(expireAt)
                .build();
    }
}
