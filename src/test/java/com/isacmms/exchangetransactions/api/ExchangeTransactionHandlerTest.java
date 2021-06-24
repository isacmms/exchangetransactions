package com.isacmms.exchangetransactions.api;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.service.ExchangeTransactionService;

import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@Import(value = { ExchangeTransactionRouter.class, ExchangeTransactionHandler.class })
public class ExchangeTransactionHandlerTest {

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
	void setUp() {
		this.client = WebTestClient.bindToRouterFunction(router.exchangeRoutes(handler)).build();
	}
	
	@BeforeEach
	void beforeEach(TestInfo testInfo) {
		log.debug(testInfo.getDisplayName());
	}
	
	// ~ Expected default behaviors
	// ===============================================================================================
	
	@Test
	@DisplayName("Test find all exchange transactions by user id expected success behavior")
	void testFindAllExchangeTransactionsByUserId_ExpectCollectionOfAllExchangeTransactions() {
		
		final BigDecimal baseValue = new BigDecimal("2.32");
		
		final ExchangeTransactionEntity entity = new ExchangeTransactionEntity();
		entity.setBaseValue(baseValue);
		
		final List<ExchangeTransactionEntity> fluxResult = Stream.of(entity).collect(Collectors.toList());
		
		
		when(this.service.findAllByUserId(1L))
			.thenReturn(Flux.fromIterable(fluxResult));
		
		
		this.client.get()
			.uri("/api/v1/exchange-transactions/1")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(ExchangeTransactionEntity.class)
			.isEqualTo(fluxResult);
	}
	
}
