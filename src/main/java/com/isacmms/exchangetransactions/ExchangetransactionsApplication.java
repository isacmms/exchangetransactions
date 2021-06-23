package com.isacmms.exchangetransactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class ExchangetransactionsApplication {

	public static void main(String[] args) {
		ReactorDebugAgent.init();
		SpringApplication.run(ExchangetransactionsApplication.class, args);
	}

}
