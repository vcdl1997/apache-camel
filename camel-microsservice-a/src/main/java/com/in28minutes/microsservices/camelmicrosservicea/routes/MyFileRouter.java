package com.in28minutes.microsservices.camelmicrosservicea.routes;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class MyFileRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		from("file:C:\\Temp\\input")
		.log("${body}")
		.to("file:C:\\Temp\\output");
	}

}
