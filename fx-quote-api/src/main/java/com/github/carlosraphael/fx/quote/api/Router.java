package com.github.carlosraphael.fx.quote.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Router {

    static final String URI = "/quotes";

    @Bean
    public RouterFunction<ServerResponse> routes(FxQuoteHandler handler) {
        return route(GET(URI), handler::getAllQuotes)
                .andRoute(GET(URI +"/{id}"), handler::getQuoteById)
                .andRoute(POST(URI), handler::createQuote);
    }
}
