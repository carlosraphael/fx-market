package com.github.carlosraphael.fx.rate.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

@Data
public class Rate {

    private long timestamp;
    private Currency base;
    private Map<Currency, BigDecimal> rates;

}
