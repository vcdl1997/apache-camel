package com.in28minutes.microsservices.camelmicrosservicea.routes;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class RestApiConsumerRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		restConfiguration().host("localhost").port(52713);
		
		from("timer:rest-api-consumer?period=10000")
			.setHeader("from", () -> "EUR")
			.setHeader("to", () -> "INR")
			.to("rest:get:currency-exchange/from/{from}/to/{to}")
			.log("${body}")
		;
	}

}
