package com.in28minutes.microsservices.camelmicrosserviceb.routes;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class KafkaReceiverRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		from("kafka:myKafkaTopic")
			.log("${body}")
			.to("file:C:\\Temp\\output?fileName=${date:now:ddMMyyyyhhmmss}.txt")
		;
	}

}
