package com.isacmms.exchangetransactions.integration.service;

import java.math.BigDecimal;

import reactor.core.publisher.Mono;

public interface ExchangeRatesApiService {

	Mono<BigDecimal> fetchCurrencyRate(String baseCurrency, String rateCurrency);
	
}
