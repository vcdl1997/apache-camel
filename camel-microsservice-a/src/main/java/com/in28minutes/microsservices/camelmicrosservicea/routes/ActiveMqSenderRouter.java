package com.in28minutes.microsservices.camelmicrosservicea.routes;

import java.util.Random;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqSenderRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		// Adding a new message to the queue every 10 seconds
		//from("timer:send-message?period=10000")
		//.bean(MessageQueue.class, "addMessage")
		//.log("${body}")
		//.to("activemq:teste")
		//;
		
		
		
		// Monitoring json folder and moving files from it to queue
		//from("file:C:\\Temp\\json")
		//.log("${body}")
		//.to("activemq:teste")
		//;
		
		
		
		from("file:C:\\Temp\\xml")
			.log("${body}")
			.to("activemq:queue-xml")
		;
	}
}

@Component
class MessageQueue{
	
	public String addMessage() {
		return "Message Number: " + Math.abs(new Random().nextInt());
	}
}
