package com.isacmms.exchangetransactions.integration.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRatesApiResponse {

	private boolean success;
	
	private Instant timestamp;
	
	private String base;
	
	private Date date;
	
	private Map<String, BigDecimal> rates;
	
}
