package com.isacmms.exchangetransactions.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.repository.ExchangeTransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ExchangeTransactionServiceImpl implements ExchangeTransactionService {
	
	private static final Logger logger = LoggerFactory.getLogger(ExchangeTransactionServiceImpl.class);
	
	private final ExchangeTransactionRepository repository;
	
	public ExchangeTransactionServiceImpl(ExchangeTransactionRepository repository) {
		this.repository = repository;
	}

	/**
	 * Retrieve all transactions of a given user by its id
	 * 
	 * @param userId id of the owner of the transaction to be retrieved
	 * @return all transactions found of a given user
	 */
	@Override
	public Flux<ExchangeTransactionEntity> findAllByUserId(Long userId) {
		logger.debug("> ExchangeTransactionServiceImpl.findAllByUserId()");
		
		return this.repository.findAllByUserId(userId);
	}
	
	/**
	 * Creation of new transaction
	 * 
	 * @param transaction exchange transaction to persist
	 * @param userId id of the owner of the transaction
	 * @return persisted entity
	 */
	@Override
	@Transactional
	public Mono<ExchangeTransactionEntity> create(ExchangeTransactionEntity dto) {
		logger.debug("> ExchangeTransactionServiceImpl.create()");
		
		return this.repository.save(dto);
	}

}
