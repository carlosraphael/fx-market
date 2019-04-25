package com.github.carlosraphael.fx.rate;

import com.github.carlosraphael.fx.rate.api.FxRateHandler;
import com.github.carlosraphael.fx.rate.service.FxRateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Currency;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

	@LocalServerPort
	int port;

	@SpyBean
	FxRateHandler fxRateHandler;
	@SpyBean
	FxRateService fxRateService;
	@SpyBean
	WebClient webClient; // service dependency

	WebTestClient webTestClient;

	@Before
	public void setup() {
		webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	public void shouldReturnResponseWhenRequestContainsSourceCurrencyAndTargetCurrency() {
		// given
		Currency sourceCurrency = Currency.getInstance("EUR");
		Currency targetCurrency = Currency.getInstance("USD");

		// when
		StatusAssertions response = webTestClient.get()
				.uri("/rates?source={0}&target={1}", sourceCurrency.getCurrencyCode(), targetCurrency.getCurrencyCode())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus();

		// then
		assertThat(response).isOk()
				.expectBody()
					.jsonPath("$.source").isEqualTo(sourceCurrency.getCurrencyCode())
					.jsonPath("$.target").isEqualTo(targetCurrency.getCurrencyCode())
					.jsonPath("$.rate").isNotEmpty()
					.jsonPath("$.time").isNotEmpty();

		verify(fxRateHandler, times(1)).getRate(any(ServerRequest.class));
		verify(fxRateService, times(1)).getRate(eq(sourceCurrency), eq(targetCurrency));
		verify(webClient, times(1)).get();
	}

	// TODO: more scenarios

	private static <T> T assertThat(T t) {
		return t;
	}

}
