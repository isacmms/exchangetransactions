package com.isacmms.exchangetransactions.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ExchangeTransactionRouter {
	
	@Bean
    RouterFunction<ServerResponse> exchangeRoutes(ExchangeTransactionHandler handler) {
		
        return route(GET("/api/v1/exchange-transactions/{userId}"), handler::findAll);
    }

}
