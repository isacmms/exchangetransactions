package com.isacmms.exchangetransactions.integration.model;

import java.util.Map;

public class ExchangeRatesApiErrorDetails {

	private  Map<String, String> error;
	
	public ExchangeRatesApiErrorDetails() {}
	
	public Map<String, String> getError() {
		return this.error;
	}
	
	public String getErrorMsg() {
		return this.error.get("code");
	}
	
}
