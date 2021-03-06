package com.github.carlosraphael.fx.rate.service;

import com.github.carlosraphael.fx.rate.config.WebClientConfig;
import com.github.carlosraphael.fx.rate.domain.FxRate;
import com.github.carlosraphael.fx.rate.service.impl.FxRateServiceImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@EnableCaching
@Import({WebClientConfig.class, FxRateServiceImpl.class})
@TestPropertySource(properties = {
        "fxRate.api.URI=/api/v7/convert?q={currencyQuery}&compact=ultra&apiKey=API_KEY",
        "fxRate.api.baseURL=http://localhost:24596",
        "spring.cache.cache-names=rates",
        "spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=60s"
})
@SpringBootTest
public class FxRateServiceTest {

    MockWebServer mockServer;

    @Autowired
    FxRateService fxRateService;
    @SpyBean
    WebClient webClient;

    @Before
    public void setup() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start(24596);
    }

    @After
    public void shutdown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    public void shouldReturnFxRateWhenSourceCurrencyIsEURAndTargetCurrencyIsUSD() {
        // given
        Currency sourceCurrency = Currency.getInstance("EUR");
        Currency targetCurrency = Currency.getInstance("USD");
        prepareResponse(response -> response.setResponseCode(200).setBody(basedOn("json/EUR_USD.json")));

        // when
        Mono<FxRate> fxRateMono = fxRateService.getRate(sourceCurrency, targetCurrency);

        // then
        assertThat(fxRateMono.block())
                .isNotNull()
                .extracting("rate", "source", "target")
                .containsOnly(new BigDecimal("1.124483"), sourceCurrency, targetCurrency);
        verify(webClient, times(1)).get();
    }

    @Test
    public void shouldReturnCachedFxRateWhenServiceIsCalledForTheSecondTime() {
        // given
        Currency sourceCurrency = Currency.getInstance("USD");
        Currency targetCurrency = Currency.getInstance("EUR");
        prepareResponse(response -> response.setResponseCode(200).setBody(basedOn("json/USD_EUR.json")));

        // when
        Mono<FxRate> fxRateMonoFirstCall = fxRateService.getRate(sourceCurrency, targetCurrency);
        Mono<FxRate> fxRateMonoSecondCall = fxRateService.getRate(sourceCurrency, targetCurrency);

        // then
        assertThat(fxRateMonoFirstCall.block()).isSameAs(fxRateMonoSecondCall.block());
        verify(webClient, times(1)).get();
    }

    private void prepareResponse(Consumer<MockResponse> consumer) {
        MockResponse response = new MockResponse();
        response.setHeader("Content-Type", "application/json");
        consumer.accept(response);
        mockServer.enqueue(response);
    }

    private String basedOn(String jsonFile) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource(jsonFile).getInputStream(), StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
