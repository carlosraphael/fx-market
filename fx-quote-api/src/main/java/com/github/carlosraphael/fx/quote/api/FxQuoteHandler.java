package com.github.carlosraphael.fx.quote.api;

import com.github.carlosraphael.fx.quote.domain.CreateQuote;
import com.github.carlosraphael.fx.quote.domain.FxQuote;
import com.github.carlosraphael.fx.quote.service.FxQuoteService;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@AllArgsConstructor
public class FxQuoteHandler {

    private final FxQuoteService fxQuoteService;

    public Mono<ServerResponse> createQuote(ServerRequest request) {
        return defaultWriteOperationHandler(
                request.bodyToFlux(CreateQuote.class).flatMap(fxQuoteService::create)
        );

    }

    public Mono<ServerResponse> getQuoteById(ServerRequest request) {
        return defaultReadOperationHandler(
                fxQuoteService.getQuoteById(request.pathVariable("id"))
        );
    }

    public Mono<ServerResponse> getAllQuotes(ServerRequest request) {
        return defaultReadOperationHandler(
                fxQuoteService.getQuotes()
        );
    }

    private Mono<ServerResponse> defaultWriteOperationHandler(Publisher<FxQuote> quotes) {
        return Mono.from(quotes)
                .flatMap(fxQuote -> ServerResponse
                        .created(URI.create("/quotes/" + fxQuote.getId()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    private Mono<ServerResponse> defaultReadOperationHandler(Publisher<FxQuote> quotes) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(quotes, FxQuote.class);
    }

}
