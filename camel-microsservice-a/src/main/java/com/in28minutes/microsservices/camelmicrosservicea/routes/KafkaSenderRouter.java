package com.in28minutes.microsservices.camelmicrosservicea.routes;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class KafkaSenderRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		from("file:C:\\Temp\\json")
			.log("${body}")
			.to("kafka:myKafkaTopic")
		;
	}

}
