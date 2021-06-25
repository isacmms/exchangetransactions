package com.isacmms.exchangetransactions.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isacmms.exchangetransactions.integration.service.ExchangeRatesApiService;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.repository.ExchangeTransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ExchangeTransactionServiceImpl implements ExchangeTransactionService {
	
	private static final Logger logger = LoggerFactory.getLogger(ExchangeTransactionServiceImpl.class);
	
	private final ExchangeTransactionRepository repository;
	private final ExchangeRatesApiService externalApiService;
	
	public ExchangeTransactionServiceImpl(
			ExchangeTransactionRepository repository,
			ExchangeRatesApiService externalApiService) {
		this.repository = repository;
		this.externalApiService = externalApiService;
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
		
		final String baseCurrency = dto.getBaseCurrency();
		final String rateCurrency = dto.getRateCurrency();
		
		return Mono.zip(
				Mono.just(dto),
				this.findConversionRate(baseCurrency, rateCurrency),
				(_dto, usedRate) -> {
					_dto.setUsedRate(usedRate);
					return _dto;
				})
				.flatMap(_dto -> this.repository.save(_dto));
	}
	
	/**
	 * Fetch currency rates from external service
	 * 
	 * @param baseCurrency
	 * @param rateCurrency
	 * @return conversion rate from the base currency to the rate currency
	 */
	@Override
	public Mono<BigDecimal> findConversionRate(String baseCurrency, String rateCurrency) {
		logger.debug("> ExchangeRateTransactionServiceImpl.findConversionRate()");
		
		return this.externalApiService.fetchCurrencyRate(baseCurrency, rateCurrency);
	}

}
