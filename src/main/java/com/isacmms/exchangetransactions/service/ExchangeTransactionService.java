package com.isacmms.exchangetransactions.service;

import java.math.BigDecimal;

import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeTransactionService {

	Flux<ExchangeTransactionEntity> findAllByUserId(Long userId);
	Mono<ExchangeTransactionEntity> create(ExchangeTransactionEntity transaction);
	Mono<BigDecimal> findConversionRate(String baseCurrency, String rateCurrency);
	
}
