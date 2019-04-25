package com.github.carlosraphael.fx.rate.api;

import com.github.carlosraphael.fx.rate.domain.FxRate;
import com.github.carlosraphael.fx.rate.service.FxRateService;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
public class FxRateHandlerTest {

    @Mock
    FxRateService fxRateService;
    @InjectMocks
    FxRateHandler fxRateHandler;

    @Test
    public void shouldReturnResponseWhenRequestContainsSourceCurrencyAndTargetCurrency() {
        // given
        BigDecimal expectedRate = BigDecimal.ONE;
        Currency sourceCurrency = Currency.getInstance("EUR");
        Currency targetCurrency = Currency.getInstance("USD");
        MockServerRequest serverRequest = mockServerRequest(sourceCurrency, targetCurrency);
        FxRate expectedFxRate = createExpectedFxRate(expectedRate, sourceCurrency, targetCurrency);
        given(fxRateService.getRate(eq(sourceCurrency), eq(targetCurrency))).willReturn(Mono.just(expectedFxRate));

        // when
        Mono<ServerResponse> serverResponse = fxRateHandler.getRate(serverRequest);

        // then
        assertThat(serverResponse).isNotNull();
        assertThat(serverResponse.block())
                .extracting("statusCode", "entity.value")
                .containsOnly(HttpStatus.OK.value(), expectedFxRate);
        then(fxRateService).should(times(1)).getRate(eq(sourceCurrency), eq(targetCurrency));
    }

    @Test
    public void shouldThrowExceptionWhenMissingSourceCurrency() {
        // given
        Currency sourceCurrency = null;
        Currency targetCurrency = Currency.getInstance("USD");
        MockServerRequest serverRequest = mockServerRequest(sourceCurrency, targetCurrency);

        // when
        ThrowableAssert.ThrowingCallable callableFunction = () -> fxRateHandler.getRate(serverRequest);

        // then
        assertThatExceptionOfType(ServerWebInputException.class)
                .isThrownBy(callableFunction)
                .withNoCause()
                .withMessage("400 BAD_REQUEST \"Missing source currency\""); // FIXME: enhance error response
        then(fxRateService).should(never()).getRate(eq(sourceCurrency), eq(targetCurrency));
    }

    @Test
    public void shouldThrowExceptionWhenMissingTargetCurrency() {
        // given
        Currency sourceCurrency = Currency.getInstance("EUR");
        Currency targetCurrency = null;
        MockServerRequest serverRequest = mockServerRequest(sourceCurrency, targetCurrency);

        // when
        ThrowableAssert.ThrowingCallable callableFunction = () -> fxRateHandler.getRate(serverRequest);

        // then
        assertThatExceptionOfType(ServerWebInputException.class)
                .isThrownBy(callableFunction)
                .withNoCause()
                .withMessage("400 BAD_REQUEST \"Missing target currency\""); // FIXME: enhance error response
        then(fxRateService).should(never()).getRate(eq(sourceCurrency), eq(targetCurrency));
    }

    private FxRate createExpectedFxRate(BigDecimal expectedRate, Currency sourceCurrency, Currency targetCurrency) {
        return FxRate.builder()
                .source(sourceCurrency)
                .target(targetCurrency)
                .rate(expectedRate)
                .build();
    }

    private MockServerRequest mockServerRequest(Currency sourceCurrency, Currency targetCurrency) {
        return MockServerRequest.builder()
                .queryParam("source", sourceCurrency != null ? sourceCurrency.getCurrencyCode() : "")
                .queryParam("target", targetCurrency != null ? targetCurrency.getCurrencyCode() : "")
                .build();
    }
}
