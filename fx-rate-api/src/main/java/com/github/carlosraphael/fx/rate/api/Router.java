package com.github.carlosraphael.fx.rate.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Router {

    static final String URI = "/rates";

    @Bean
    public RouterFunction<ServerResponse> routes(FxRateHandler handler) {
        return route(GET(URI), handler::getRate);
    }
}
