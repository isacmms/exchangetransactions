package com.isacmms.exchangetransactions.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isacmms.exchangetransactions.integration.service.ExchangeRatesApiService;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity.CurrencyEnum;
import com.isacmms.exchangetransactions.repository.ExchangeTransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ExchangeTransactionServiceImplTest {
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeTransactionServiceImplTest.class);

	@Mock
	private ExchangeTransactionRepository repository;
	@Mock
	private ExchangeRatesApiService externalService;
	@InjectMocks
	private ExchangeTransactionServiceImpl service;
	
	// ~ Set up
	// ========================================================
	
	@BeforeEach
	void beforeEach(TestInfo testInfo) {
		log.debug(testInfo.getDisplayName());
	}
	
	// ~ Expected default behaviors
	// ===============================================================================================
	
	@Test
	@DisplayName("Test find all exchange transactions by user id expected success behavior")
	void testFindAllExchangeTransactionsByUserId_ExpectCollectionOfAllExchangeTransactions() {
		final Long userId = 1L;
		
		final String baseCurrency = CurrencyEnum.BRL.name();
		final BigDecimal baseValue = new BigDecimal("2.1");
		
		final String rateCurrency = CurrencyEnum.USD.name();
		
		final ExchangeTransactionEntity entity = new ExchangeTransactionEntity(
				userId, 
				baseCurrency, baseValue, 
				rateCurrency);
		
		when(this.repository.findAllByUserId(anyLong()))
			.thenReturn(Flux.fromStream(Stream.of(entity)));
		
		final Flux<ExchangeTransactionEntity> fluxResult = this.service.findAllByUserId(userId);
		
		StepVerifier.create(fluxResult)
			.assertNext(transactionResult -> assertAll(
					() -> assertEquals(userId, transactionResult.getUserId()),
					() -> assertEquals(baseCurrency, transactionResult.getBaseCurrency()),
					() -> assertEquals(baseValue, transactionResult.getBaseValue()),
					() -> assertEquals(rateCurrency, transactionResult.getRateCurrency())))
			.expectComplete()
			.log()
			.verify();
	}
	
	/**
	 * This shouldn't assert rateValue nor usedRate, since any input from handler dto should be ignore.
	 * usedRate should be fetched from external api and rateValue calculated after that.
	 */
	@Test
	@DisplayName("Test create exchange transaction expected success behavior")
	void testCreateExchangeTransaction_ExpectExchangeTransaction() {
		final Long userId = 1L;
		
		final String baseCurrency = CurrencyEnum.BRL.name();
		final BigDecimal baseValue = new BigDecimal("2.1");
		
		final String rateCurrency = CurrencyEnum.USD.name();
		final BigDecimal usedRate = new BigDecimal("2.2");
		
		final ExchangeTransactionEntity entity = new ExchangeTransactionEntity(
				userId, 
				baseCurrency, baseValue, 
				rateCurrency);
		
		when(this.repository.save(any(ExchangeTransactionEntity.class)))
			.thenReturn(Mono.just(entity));
		when(this.externalService.fetchCurrencyRate(anyString(), anyString()))
			.thenReturn(Mono.just(usedRate));
		
		final Mono<ExchangeTransactionEntity> monoResult = this.service.create(entity);
		
		StepVerifier.create(monoResult)
			.assertNext(transactionResult -> assertAll(
					() -> assertEquals(userId, transactionResult.getUserId()),
					() -> assertEquals(baseCurrency, transactionResult.getBaseCurrency()),
					() -> assertEquals(baseValue, transactionResult.getBaseValue()),
					() -> assertEquals(rateCurrency, transactionResult.getRateCurrency()),
					() -> assertEquals(usedRate, transactionResult.getUsedRate())))
			.expectComplete()
			.log()
			.verify();
	}
	
}
