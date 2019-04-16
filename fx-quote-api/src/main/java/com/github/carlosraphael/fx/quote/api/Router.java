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

    @Bean
    public RouterFunction<ServerResponse> routes(FxQuoteHandler handler) {
        return route(GET("/quotes"), handler::getAllQuotes)
                .andRoute(GET("/quotes/{id}"), handler::getQuoteById)
                .andRoute(POST("/quotes"), handler::createQuote);
    }
}
