package com.isacmms.exchangetransactions.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.repository.ExchangeTransactionRepository;

import reactor.core.publisher.Flux;

@Service
public class ExchangeTransactionServiceImpl implements ExchangeTransactionService {
	
	private final ExchangeTransactionRepository repository;
	
	private static final Logger logger = LoggerFactory.getLogger(ExchangeTransactionServiceImpl.class);
	
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
		logger.debug("> ExchangeTransactionServiceImplTest.findAllByUserId()");
		
		return this.repository.findAllByUserId(userId);
	}

}
