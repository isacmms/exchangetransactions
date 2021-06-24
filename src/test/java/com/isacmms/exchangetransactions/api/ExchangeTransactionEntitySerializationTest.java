package com.isacmms.exchangetransactions.api;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity.CurrencyEnum;

public class ExchangeTransactionEntitySerializationTest {

	private static final Logger log = LoggerFactory.getLogger(ExchangeTransactionEntitySerializationTest.class);
	
	private ObjectMapper mapper = new ObjectMapper();
	
	// ~ Set up
	// ========================================================
	
	@BeforeEach
	void beforeEach(TestInfo testInfo) {
		log.debug(testInfo.getDisplayName());
	}
	
	// TODO test json ignore
	@Test
	@DisplayName("Test exchange transaction with readonly fields expected readonly fields ignored")
	void testExchangeTransactionWithReadonlyFields_ExpectReadonlyFieldsIgnored() 
			throws JsonMappingException, JsonProcessingException {
		
		Long userId = 1L;
		String baseCurrency = CurrencyEnum.BRL.name();
		BigDecimal baseValue = new BigDecimal("1.32");
		String rateCurrency = CurrencyEnum.USD.name();
		BigDecimal rateValue = new BigDecimal("3.21");
		BigDecimal usedRate = new BigDecimal("2.22");
		
		String serializedObj = 
				"{"
					+ String.format("\"userId\": %d, ", userId)
					+ String.format("\"baseCurrency\": \"%s\", ", baseCurrency)
					+ String.format("\"baseValue\": %s, ", baseValue)
					+ String.format("\"rateCurrency\": \"%s\", ", rateCurrency)
					+ String.format("\"rateValue\": %s,", rateValue)
					+ String.format("\"usedRate\": %s", usedRate)
				+ "}";

		log.debug(serializedObj);
		ExchangeTransactionEntity deserializedObj = this.mapper.readValue(serializedObj, ExchangeTransactionEntity.class);

	    assertAll(
				() -> assertEquals(userId, deserializedObj.getUserId()),
				() -> assertEquals(baseCurrency, deserializedObj.getBaseCurrency()),
				() -> assertEquals(baseValue, deserializedObj.getBaseValue()),
				() -> assertEquals(rateCurrency, deserializedObj.getRateCurrency()),
				
				() -> assertNull(deserializedObj.getRateValue()),
				() -> assertNull(deserializedObj.getUsedRate()));
		
		
	}
	
}
