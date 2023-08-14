package com.in28minutes.microsservices.camelmicrosserviceb.routes;

import java.math.BigDecimal;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
public class ActiveMqConsumerRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		// creating .txt file from queue message
		//from("activemq:teste")
		//.bean(MessageQueue.class, "process")
		//.to("file:C:\\Temp\\output?fileName=${date:now:ddMMyyyyhhmmss}.txt");
		
		
		
		/* Converting queue message into java object, 
		 * performing processing and transformations
		 * */
		//from("activemq:teste")
		//.unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
		//.bean(MyCurrencyExchangeProcessor.class, "process")
		//.bean(MyCurrencyExchangeTransformer.class, "transform")
		//.to("log:received")
		//;
		
		
		from("activemq:queue-xml")
			.unmarshal()
			.jacksonXml(CurrencyExchange.class)
			.to("log:received")
		;
	}
}


@Component
class MessageQueue{
	
	private final Logger log = LoggerFactory.getLogger(MessageQueue.class);
	
	public void process(Exchange exchange) {
		log.info("Conteudo da mensagem processada {}", exchange.getMessage().getBody());
	}
}


@Component
class MyCurrencyExchangeProcessor {

	private final Logger log = LoggerFactory.getLogger(MyCurrencyExchangeProcessor.class);
	
	
	public void process(CurrencyExchange currencyExchange) {

		log.info(
			"Do some processing with currencyExchange.getConversionMultiple() value which is {}", 
			currencyExchange.getConversionMultiple()
		);
	}
}


@Component
class MyCurrencyExchangeTransformer {

	public CurrencyExchange transform(CurrencyExchange currencyExchange) {
		
		var multiple = currencyExchange.getConversionMultiple().multiply(BigDecimal.TEN);
		
		currencyExchange.setConversionMultiple(multiple);
		
		return currencyExchange;
	}
}
