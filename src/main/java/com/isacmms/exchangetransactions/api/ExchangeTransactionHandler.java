package com.isacmms.exchangetransactions.api;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.service.ExchangeTransactionService;

import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

@Component
public class ExchangeTransactionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeTransactionHandler.class);
	
	private final ExchangeTransactionService service;
	
	private final ObjectMapper mapper;
	private final Validator validator;
	
	private static final String BODY_REQUIRED_MSG = "Request body is required";
	
	public ExchangeTransactionHandler(ExchangeTransactionService service, ObjectMapper mapper, Validator validator) {
		this.service = service;
		this.mapper = mapper;
		this.validator = validator;
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
	 * New entity creation
	 * 
	 * @param req incoming request
	 * @return response of the created entity
	 */
	public Mono<ServerResponse> create(ServerRequest req) {
		log.debug("> ExchangeTransactionHandler.create()");
		
		return defaultCreatedResponse(
			extractBodyRequired(req)
				.handle(this::validate)
				.flatMap(transaction -> this.service.create(transaction)));
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
	 * Default Http POST response
	 * 
	 * @param publisher of the result entity
	 * @return response of the created entity
	 */
	private static Mono<ServerResponse> defaultCreatedResponse(Publisher<ExchangeTransactionEntity> publisher) {
		log.debug("> ExchangeTransactionHandler.defaultCreatedResponse()");
		
		return Mono.from(publisher)
				.flatMap(exchangeRateTransaction -> 
						ServerResponse
							.created(URI.create("/api/v1/exchange-transactions/" + exchangeRateTransaction.getId()))
							.contentType(MediaType.APPLICATION_JSON)
							.body(Mono.just(exchangeRateTransaction), ExchangeTransactionEntity.class));
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
	
	/**
	 * Extracts request's body.
	 * Body <i>must <b>not</b> be empty</i> otherwise bad request error response is sent.
	 * 
	 * @param req incoming request to extract the body from
	 * @return mono of the unmarshalled ExchangeRateTransaction object or NoSuchElementException
	 */
	private static Mono<ExchangeTransactionEntity> extractBodyRequired(ServerRequest req) {
		return req
				.bodyToMono(ExchangeTransactionEntity.class)
				.switchIfEmpty(Mono.defer(() -> 
						Mono.error(new BadRequestException(BODY_REQUIRED_MSG))))
				.single();
	}
	
	/**
	 * Bean validation of the entity object. Throws 422 UnprocessableEntityException if any validation error is found
	 * 
	 * @param transaction entity object to be validated
	 * @return validated entity object if no error is found
	 */
	private void validate(ExchangeTransactionEntity transaction, SynchronousSink<ExchangeTransactionEntity> sink) {
		log.debug("> ExchangeTransactionHandler.validate()");
		
		final Errors errors = new BeanPropertyBindingResult(transaction, ExchangeTransactionEntity.class.getName());
		validator.validate(transaction, errors);
		
		if (errors == null || errors.getAllErrors().isEmpty()) {
			sink.next(transaction);
		}
		else {
			final Map<String, String> mappedErrors = errors.getAllErrors()
					.stream()
					.collect(Collectors.toMap(
							err -> err instanceof FieldError ? 
									((FieldError) err).getField() : "Field error",
							err -> err instanceof FieldError ? 
									((FieldError) err).getDefaultMessage() : "Not valid"));
			
			String errMsg = "";
			try {
				errMsg = mapper.writeValueAsString(mappedErrors);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				errMsg = mappedErrors.toString();
			}
			sink.error(new UnprocessableEntityException(errMsg));
		}
	}

}
