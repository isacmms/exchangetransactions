package com.isacmms.exchangetransactions.api;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.service.ExchangeTransactionService;

import lombok.extern.log4j.Log4j2;

import reactor.core.publisher.Mono;

@Log4j2
@Component
public class ExchangeTransactionHandler {
	
	private final ExchangeTransactionService service;
	
	public ExchangeTransactionHandler(ExchangeTransactionService service) {
		this.service = service;
	}
	
	/**
	 * Retrieve all entities by its owner's id
	 * 
	 * @param req incoming request
	 * @return all found entities from a given owner
	 */
	public Mono<ServerResponse> findAll(ServerRequest req) {
		log.debug("> ExchangeTransactionHandler.findAll()");

		return defaultOkManyResponse(
				this.service.findAllByUserId(userId(req)));
	}
	
	/**
	 * Default Http GET many response
	 * 
	 * @param publisher of the result entities
	 * @return response of the found entities
	 */
	private static Mono<ServerResponse> defaultOkManyResponse(Publisher<ExchangeTransactionEntity> publisher) {
		log.debug("> ExchangeTransactionHandler.defaultOkManyResponse()");
		
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(publisher, ExchangeTransactionEntity.class);
	}
	
	/**
	 * Extracts userId from request path variables
	 * 
	 * @param req incoming request to extract id path variable from
	 * @return id extracted id from request's path variable
	 */
	private static Long userId(ServerRequest req) {
		
		final String id = req.pathVariable("userId");
		return Long.valueOf(id);
	}

}
