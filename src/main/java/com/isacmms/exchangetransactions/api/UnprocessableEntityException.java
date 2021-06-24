package com.isacmms.exchangetransactions.api;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

public class UnprocessableEntityException extends ResponseStatusException {
	
	private static final long serialVersionUID = 1L;

	public UnprocessableEntityException() {
		this(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());
	}
	
	public UnprocessableEntityException(@Nullable String message) {
		super(HttpStatus.UNPROCESSABLE_ENTITY, message);
	}
	
}
