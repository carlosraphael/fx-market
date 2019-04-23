package com.github.carlosraphael.fx.rate.api;

import com.github.carlosraphael.fx.rate.domain.FxRate;
import com.github.carlosraphael.fx.rate.service.FxRateService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Currency;

@Component
@AllArgsConstructor
public class FxRateHandler {

    private final FxRateService fxRateService;

    public Mono<ServerResponse> getRate(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        fxRateService.getRate(getSourceCurrency(serverRequest), getTargetCurrency(serverRequest)),
                        FxRate.class
                );
    }

    private Currency getSourceCurrency(ServerRequest serverRequest) {
        return getCurrency(serverRequest, "source");
    }

    private Currency getTargetCurrency(ServerRequest serverRequest) {
        return getCurrency(serverRequest, "target");
    }

    private Currency getCurrency(ServerRequest serverRequest, String attributeName) {
        return Currency.getInstance(serverRequest.attribute(attributeName)
                .map(this::trimToNull)
                .orElseThrow(() -> new ServerWebInputException("Missing " + attributeName + " currency"))
        );
    }

    private String trimToNull(Object object) {
        return object == null ? null : StringUtils.trimToNull(object.toString());
    }
}
