package com.isacmms.exchangetransactions.data;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;

import com.isacmms.exchangetransactions.config.TestDBInitializerConfig;
import com.isacmms.exchangetransactions.model.ExchangeTransactionEntity;
import com.isacmms.exchangetransactions.repository.ExchangeTransactionRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import({ TestDBInitializerConfig.class })
class R2dbcTest {
	
	private static final Logger log = LoggerFactory.getLogger(R2dbcTest.class);

	@Autowired
    private DatabaseClient databaseClient;
	
    @Autowired
    private ExchangeTransactionRepository repository;
    
    // ~ Set up
 	// ========================================================
 	
 	@BeforeEach
 	void setUp(TestInfo testInfo) {
 		log.debug(testInfo.getDisplayName());
 	}
    
 	// ~ Expected success behaviors
 	// ===============================================================================================
 	
    @Test
    @DisplayName("Test find all exchange transaction entities expect success")
    void testDatabaseClient() {
    	Flux<Map<String, Object>> results = this.databaseClient
    			.sql("SELECT * FROM exchange_transactions")
    			.fetch().all();
    	
    	StepVerifier.create(results)
    		.assertNext(result -> assertNotNull(result))
    		.thenConsumeWhile(el -> true)
    		.expectComplete()
    		.log()
    		.verify();
    }
    
    @Test
    @DisplayName("Test find all exchange transaction entities expect transient fields not null")
    void testTransientFields() {
    	
    	Flux<ExchangeTransactionEntity> fluxResult = this.repository.findAll();

    	StepVerifier.create(fluxResult)
    		.assertNext(result -> assertNotNull(result.getRateValue()))
    		.thenConsumeWhile(x -> true)
    		.expectComplete()
    		.log()
    		.verify();
    }
    
}
