package com.isacmms.exchangetransactions.service;

import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;

import reactor.core.publisher.Flux;

public interface ExchangeTransactionService {

	Flux<ExchangeTransactionEntity> findAllByUserId(Long userId);
	
}
