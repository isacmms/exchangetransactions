package com.isacmms.exchangetransactions.service;

import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeTransactionService {

	Flux<ExchangeTransactionEntity> findAllByUserId(Long userId);
	Mono<ExchangeTransactionEntity> create(ExchangeTransactionEntity transaction);
	
}
