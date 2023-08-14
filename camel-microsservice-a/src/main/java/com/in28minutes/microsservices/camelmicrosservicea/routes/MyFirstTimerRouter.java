package com.in28minutes.microsservices.camelmicrosservicea.routes;

import java.time.LocalDateTime;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component - comentado para desativar a execução do componente
public class MyFirstTimerRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		from("timer:first-timer")
		.log("${body}")
		.transform().constant("My Constant Message")
		.log("${body}")
		//.transform().constant("Time: " + LocalDateTime.now())
		.bean(GetCurrentTimeBean.class, "getCurrentTime")
		.log("${body}")
		.bean(SimpleLoggingProcessingComponent.class, "process")
		.log("${body}")
		.process(new SimpleLoggingProcessor())
		.to("log:first-timer");
	}
}

@Component
class GetCurrentTimeBean{
	
	public String getCurrentTime() {
		return "Time now is: " + LocalDateTime.now();
	}
}

@Component
class SimpleLoggingProcessingComponent{
	
	private final Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);
	
	public void process(String message) {
		logger.info("Processando conteudo {}", message);
	}
}

class SimpleLoggingProcessor implements Processor{

	private final Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessor.class); 
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("Processor {}", exchange.getMessage().getBody());
	}

}