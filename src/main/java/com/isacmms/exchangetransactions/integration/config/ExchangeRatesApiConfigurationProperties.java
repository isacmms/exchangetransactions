package com.isacmms.exchangetransactions.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("exchange-rates-api")
public class ExchangeRatesApiConfigurationProperties {

	/**
	 * <p> Exchange Rates Api key </p>
	 * 
	 * <p>Defaults to <i>null</i></p>
	 */
	private String apiKey;
	
	/**
	 * <p> Exchange Rates Api base url </p>
	 * 
	 * <p> defaults to http://api.exchangeratesapi.io/v1/ </p>
	 */
	private String baseUrl;
	
	/**
	 * <p> Exchange Rates Api fetch currency rates path </p>
	 * 
	 * <p> defaults to latest </p>
	 */
	private String fetchRatesPath;
	
	/**
	 * <p> Set this to true if using a free plan. Free plan only allows EUR base currency and http scheme, 
	 * requiring and indirect strategy. </p>
	 * 
	 * <p> defaults to true </p>
	 */
	private boolean freePlan;
	
	/**
	 * <p> The base currency query param name </p>
	 * 
	 * <p> defaults to base </p>
	 */
	private String baseCurrencyParamName;
	
	/**
	 * <p> The rate currency query param name </p>
	 * 
	 * <p> defaults to symbols </p>
	 */
	private String rateCurrencyParamName;
	
}
