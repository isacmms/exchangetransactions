package com.isacmms.exchangetransactions.api;

import static org.mockito.ArgumentMatchers.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity.CurrencyEnum;
import com.isacmms.exchangetransactions.service.ExchangeTransactionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@Import(value = { 
		ExchangeTransactionRouter.class, 
		ExchangeTransactionHandler.class, 
		ObjectMapper.class, 
		LocalValidatorFactoryBean.class 
})
class ExchangeTransactionHandlerTest {

	private static final Logger log = LoggerFactory.getLogger(ExchangeTransactionHandlerTest.class);
	
	@Autowired
	private ExchangeTransactionRouter router;
	
	@MockBean
	private ExchangeTransactionService service;

	private ExchangeTransactionHandler handler;

	private WebTestClient client;
	
	// ~ Set up
	// ========================================================
	
	@BeforeEach
	void setUp(TestInfo testInfo) {
		log.debug(testInfo.getDisplayName());
		this.client = WebTestClient.bindToRouterFunction(router.exchangeRoutes(handler)).build();
		
	}
	
	// ~ Expected success behaviors
	// ===============================================================================================
	
	@Test
	@DisplayName("Test find all exchange transactions by user id expected success behavior")
	void testFindAllExchangeTransactionsByUserId_ExpectCollectionOfAllExchangeTransactions() {
		
		final BigDecimal baseValue = new BigDecimal("2.32");
		
		final ExchangeTransactionEntity entity = new ExchangeTransactionEntity();
		entity.setBaseValue(baseValue);
		
		final List<ExchangeTransactionEntity> listResult = Stream.of(entity).collect(Collectors.toList());
		
		
		when(this.service.findAllByUserId(anyLong()))
			.thenReturn(Flux.fromIterable(listResult));
		
		this.client.get()
			.uri("/api/v1/exchange-transactions/1")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(ExchangeTransactionEntity.class)
			.isEqualTo(listResult);
	}
	
	@Test
	@DisplayName("Test create exchange transaction expected success behavior")
	void testCreateExchangeTransaction_ExpectEntityCreated() {
		
		final Long userId = 1L;
		
		final String baseCurrency = CurrencyEnum.BRL.name();
		final BigDecimal baseValue = new BigDecimal("2.32");
		
		final String rateCurrency = CurrencyEnum.BRL.name();
		
		final ExchangeTransactionEntity entity = new ExchangeTransactionEntity();
		entity.setUserId(userId);
		entity.setBaseCurrency(baseCurrency);
		entity.setBaseValue(baseValue);
		entity.setRateCurrency(rateCurrency);
		
		when(this.service.create(any(ExchangeTransactionEntity.class)))
			.thenReturn(Mono.just(entity));
		
		this.client.post()
			.uri("/api/v1/exchange-transactions/")
			.body(Mono.just(entity), ExchangeTransactionEntity.class)
			.exchange()
			.expectStatus()
			.isCreated()
			.expectBody(ExchangeTransactionEntity.class)
			.isEqualTo(entity);
	}
	
	// ~ Expected fail behaviors
	// ===============================================================================================
	
	@Test
	@DisplayName("Test create exchange transaction without required fields expected validation error")
	void testCreateExchangeTransactionWithoutRequiredFields_ExpectValidationError() {
		
		final Long userId = 1L;
		
		final String baseCurrency = CurrencyEnum.BRL.name();
		final BigDecimal baseValue = new BigDecimal("2.32");
		
		final String rateCurrency = CurrencyEnum.BRL.name();
		
		final String invalidBaseCurrency = "ASD";
		final String invalidRateCurrency = "QWE";
		
		// Null values
		final ExchangeTransactionEntity entity1 = new ExchangeTransactionEntity(null,	baseCurrency, 			baseValue, 	rateCurrency);
		final ExchangeTransactionEntity entity2 = new ExchangeTransactionEntity(userId, null, 					baseValue, 	rateCurrency);
		final ExchangeTransactionEntity entity3 = new ExchangeTransactionEntity(userId, baseCurrency, 			null, 		rateCurrency);
		final ExchangeTransactionEntity entity4 = new ExchangeTransactionEntity(userId, baseCurrency, 			baseValue, 	null);
		
		// Invalid values
		final ExchangeTransactionEntity entity5 = new ExchangeTransactionEntity(userId, invalidBaseCurrency, 	baseValue, 	rateCurrency);
		final ExchangeTransactionEntity entity6 = new ExchangeTransactionEntity(userId, baseCurrency, 			baseValue, 	invalidRateCurrency);
		
		Stream.of(
				entity1, entity2, entity3, 
				entity4, entity5, entity6
			).forEach(entity -> {
				this.client.post()
					.uri("/api/v1/exchange-transactions/")
					.body(Mono.just(entity), ExchangeTransactionEntity.class)
					.exchange()
					.expectStatus()
					.isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			});
		
	}
	
	@Test
	@DisplayName("Test create exchange transaction without request body expected bad request error")
	void testCreateExchangeTransactionWithoutRequestBody_ExpectBadRequestError() {

			this.client.post()
				.uri("/api/v1/exchange-transactions/")
				.exchange()
				.expectStatus()
				.isEqualTo(HttpStatus.BAD_REQUEST);
		
	}
	
}
