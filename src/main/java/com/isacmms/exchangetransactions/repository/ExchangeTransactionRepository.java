package com.isacmms.exchangetransactions.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;

import reactor.core.publisher.Flux;

@Repository
public interface ExchangeTransactionRepository extends ReactiveCrudRepository<ExchangeTransactionEntity, Long> {
	
	Flux<ExchangeTransactionEntity> findAllByUserId(Long id);
	
}
