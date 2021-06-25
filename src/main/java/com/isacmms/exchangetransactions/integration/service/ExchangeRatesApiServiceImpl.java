package com.isacmms.exchangetransactions.integration.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.isacmms.exchangetransactions.integration.model.ExchangeRatesApiErrorDetails;
import com.isacmms.exchangetransactions.integration.model.ExchangeRatesApiResponse;

import reactor.core.publisher.Mono;

@Service
public class ExchangeRatesApiServiceImpl implements ExchangeRatesApiService {
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeRatesApiServiceImpl.class);
	
	private final WebClient client;
	
	private final String apiKey;
	private final String fetchRatesPath;
	private final String baseCurrencyParamName;
	private final String rateCurrencyParamName;
	private final boolean freePlan;

	public ExchangeRatesApiServiceImpl(
			WebClient.Builder clientBuilder,
			@Value("${exchange-rates-api.api-key:#{''}}") String apiKey,
			@Value("${exchange-rates-api.base-url:#{'http://api.exchangeratesapi.io/v1'}}") String baseUrl,
			@Value("${exchange-rates-api.fetch-rates-path:#{'latest'}}") String fetchRatesPath,
			@Value("${exchange-rates-api.base-currency-param-name:#{'base'}}") String baseCurrencyParamName,
			@Value("${exchange-rates-api.rate-currency-param-name:#{'symbols'}}") String rateCurrencyParamName,
			@Value("${exchange-rates-api.free-plan:#{'true'}}") boolean freePlan) {
		this.client = clientBuilder
				.baseUrl(baseUrl)
				.filter(logRequest())
				.build();
		this.apiKey = apiKey;
		this.fetchRatesPath = fetchRatesPath;
		this.baseCurrencyParamName = baseCurrencyParamName;
		this.rateCurrencyParamName = rateCurrencyParamName;
		this.freePlan = freePlan;
		
	}
	
	// TODO change strategy based on paid plan, changing scheme to https and using entity base currency directly
	/**
	 * Fetch currency conversion rate from exchangeratesapi.io
	 * 
	 * @param baseCurrency the currency to convert from
	 * @param rateCurrency the currency to convert to
	 */
	@Override
	public Mono<BigDecimal> fetchCurrencyRate(String baseCurrency, String rateCurrency) {
		log.debug("> ExchangeRatesApiServiceImpl.fetchCurrencyRate()");
		
		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		final String symbols = String.format("%s,%s", baseCurrency, rateCurrency);
		queryParams.add("access_key", this.apiKey);
		queryParams.add(this.baseCurrencyParamName, "EUR");
		queryParams.add(this.rateCurrencyParamName, symbols);

		return this.client.get()
			.uri(builder -> 
					builder.scheme("http")
					.path(this.fetchRatesPath)
					.queryParams(queryParams)
					.build())
			.exchangeToMono(response -> {
				if (response.statusCode().is4xxClientError()) {
					return response
							.bodyToMono(ExchangeRatesApiErrorDetails.class)
							.flatMap(errorDetails -> 
								Mono.defer(() -> 
									Mono.error(new ResponseStatusException(response.statusCode(), errorDetails.getErrorMsg()))));
				} // TODO else if (response.statusCode().is5xxClientError()) -> fallback to another api
				else {
					return response.bodyToMono(ExchangeRatesApiResponse.class);
				}
			})
			.flatMap(resp -> this.getUsedRateFromResponse(resp, baseCurrency, rateCurrency));
		
	}
	
	/**
	 * Calculate the conversion rate between the given baseCurrency and rateCurrency using the base available for the free plan (usually EUR)
	 * This is required using the free plan because not all currencies are available to be used as base, so the actual rate needs to be calculated
	 *  
	 * @param response the response received from the external api 
	 * @param baseCurrency the currency to calculate the conversion rate from
	 * @param rateCurrency the currency to calculate the conversion rate to
	 * @return the calculated rate between the baseCurrency and the rateCurrency
	 */
	private Mono<BigDecimal> getUsedRateFromResponse(ExchangeRatesApiResponse response, String baseCurrency, String rateCurrency) {
		log.debug("> ExchangeRatesApiServiceImpl.getUsedRateFromResponse()");
		
		final Map<String, BigDecimal> _rateCurrencies = response.getRates();

		final BigDecimal baseCurrencyRate = _rateCurrencies.get(rateCurrency);
		final BigDecimal rateCurrencyRate = _rateCurrencies.get(baseCurrency);
		
		final BigDecimal usedRate = rateCurrencyRate.divide(baseCurrencyRate, MathContext.DECIMAL128);
		
		return Mono.just(usedRate);
	}
	
	/**
	 * Filter to log external api response
	 * 
	 * @return configured log filter
	 */
	private static ExchangeFilterFunction logRequest() {
		log.debug("> ExchangeRatesApiServiceImpl.logRequest()");
		
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            
            return Mono.just(clientRequest);
        });
    }
	
}