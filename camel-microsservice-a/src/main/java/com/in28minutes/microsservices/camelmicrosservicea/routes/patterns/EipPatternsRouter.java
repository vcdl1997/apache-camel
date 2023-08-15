package com.in28minutes.microsservices.camelmicrosservicea.routes.patterns;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.in28minutes.microsservices.camelmicrosservicea.dto.CurrencyExchangeDto;


@Component
public class EipPatternsRouter extends RouteBuilder{
	
	@Override
	public void configure() throws Exception {
		//Multicast Pattern
		/*
		 * Writing a file written “Test” every 10 inside two folders simultaneously
		 * */
		//from("timer:multicast?period=10000")
		//.transform().constant("Teste")
		//.multicast()
		//.to(
		//	"file:C:\\Temp\\teste1?fileName=${date:now:yyyyMMddHHmmss}.txt", 
		//	"file:C:\\\\Temp\\\\teste2?fileName=${date:now:yyyyMMddHHmmss}.txt"
		//);
		
		
		
		// Division Pattern
		/* 
		 * Reading csv file, 
		 * converting its lines into a json and dispatching them to the queue.
		 * */
		//from("file:C:\\Temp\\csv")
		//.convertBodyTo(String.class)
		//.bean(SanitizeCsvProcessor.class, "sanitize")
		//.unmarshal().csv().split(body())
		//.convertBodyTo(String.class)
		//.bean(LineCsvTransform.class, "transform")
		//.to("activemq:queue-csv")
		//;
		
		
		
		from("file:C:\\Temp\\aggregate-json")
			.unmarshal().json(JsonLibrary.Jackson, CurrencyExchangeDto.class)
			.aggregate(simple("${body.to}"), new ArrayListAggregationStrategy())
			.completionSize(3)
			//.completionTimeout(HIGHEST)
			.to("log:aggregate-json")
		;
	}
}

@Component
class ProcessOrder {
	
	private final Logger log = LoggerFactory.getLogger(ProcessOrder.class);
	
	public void process(Exchange exchange) {
		log.info("Conteudo Mensagem {}", exchange.getMessage().getBody());
	}
}

@Component
class SanitizeCsvProcessor {
	
	public String sanitize(String message) {
		
		List<String> lines = List.of(message.split("\n"));
		
		return lines
			.stream()
			.filter(x -> x.contains("\"id\",\"from\",\"to\",\"conversionMultiple\"") == false)
			.collect(Collectors.joining("\n"))
		;
	}
}

@Component
class LineCsvTransform {
	
	public String transform(String message) throws JsonProcessingException {
		
		String[] columns = message.split(",");
		
		CurrencyExchangeDto currencyExchange = new CurrencyExchangeDto(
			Long.parseLong(columns[0]),
			columns[1],
			columns[2],
			BigDecimal.valueOf(Long.parseLong(columns[3]))
		);

		ObjectWriter ow = new ObjectMapper().writer();
		return ow.writeValueAsString(currencyExchange);
	}
}

class ArrayListAggregationStrategy implements AggregationStrategy{
	
	@Override
	public Exchange aggregate(Exchange currentExchange, Exchange newExchange) {
		
		System.out.println(currentExchange);
		System.out.println(newExchange);
		
		 Object newBody = newExchange.getIn().getBody();
	        ArrayList<Object> list = null;
	        if (currentExchange == null) {
	            list = new ArrayList<Object>();
	            list.add(newBody);
	            newExchange.getIn().setBody(list);
	            currentExchange = newExchange;
	 
	        } else {
	            list = currentExchange.getIn().getBody(ArrayList.class);
	            list.add(newBody);
	         
	        }
	        return currentExchange;
    }
}