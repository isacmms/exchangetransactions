package com.isacmms.exchangetransactions.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
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

import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity.CurrencyEnum;
import com.isacmms.exchangetransactions.repository.ExchangeTransactionRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ExchangeTransactionServiceImplTest {
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeTransactionServiceImplTest.class);

	@Mock
	private ExchangeTransactionRepository repository;
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
		final BigDecimal rateValue = new BigDecimal("3.2");
		
		final BigDecimal usedRate = new BigDecimal("2.32");
		
		final ExchangeTransactionEntity entity = new ExchangeTransactionEntity(
				userId, 
				baseCurrency, baseValue, 
				rateCurrency, rateValue, 
				usedRate);
		
		final List<ExchangeTransactionEntity> exchangeTransactions = Stream.of(entity).collect(Collectors.toList());
		
		when(this.repository.findAllByUserId(userId))
			.thenReturn(Flux.fromIterable(exchangeTransactions));
		
		final Flux<ExchangeTransactionEntity> fluxResult = this.service.findAllByUserId(userId);
		
		StepVerifier.create(fluxResult)
			.assertNext(transactionResult -> assertAll(
					() -> assertEquals(userId, transactionResult.getUserId()),
					() -> assertEquals(baseCurrency, transactionResult.getBaseCurrency()),
					() -> assertEquals(baseValue, transactionResult.getBaseValue()),
					() -> assertEquals(rateCurrency, transactionResult.getRateCurrency()),
					() -> assertEquals(rateValue, transactionResult.getRateValue()),
					() -> assertEquals(usedRate, transactionResult.getUsedRate())))
			.expectComplete()
			.log()
			.verify();
	}
	
}
