package com.isacmms.exchangetransactions.api;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {

	private static final long serialVersionUID = 1L;

	public BadRequestException() {
		this(HttpStatus.BAD_REQUEST.getReasonPhrase());
	}
	
	public BadRequestException(@Nullable String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}
	
}
