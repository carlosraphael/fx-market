package com.github.carlosraphael.fx.quote.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(@Value("${service.fx.rate}") String url,
                               LoadBalancerExchangeFilterFunction loadBalancerExchangeFilterFunction) {
        return WebClient.builder()
                .filter(loadBalancerExchangeFilterFunction)
                .baseUrl(url)
                .build();
    }
}
