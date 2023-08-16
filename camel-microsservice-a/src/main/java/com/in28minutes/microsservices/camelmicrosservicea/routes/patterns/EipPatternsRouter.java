package com.in28minutes.microsservices.camelmicrosservicea.routes.patterns;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.in28minutes.microsservices.camelmicrosservicea.dto.CurrencyExchangeDto;


@Component
public class EipPatternsRouter extends RouteBuilder{
	
	private DynamicRouterBean dynamicRouterBean;
	
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
		
		
		// Aggregating Pattern
		/*
		 * Aggregating message contents into a single message
		 * */
		//from("file:C:\\Temp\\aggregate-json")
		//.unmarshal().json(JsonLibrary.Jackson, CurrencyExchangeDto.class)
		//.aggregate(simple("${body.to}"), new ArrayListAggregationStrategy())
		//.completionSize(3)
		//.completionTimeout(HIGHEST)
		//.to("log:aggregate-json")
		//;
		
		
		
		// Routing list Pattern
		/*
		 * Redirecting a test message to two endpoints at the same time
		 * */
		//String routingSlip = "direct:endpoint1,direct:endpoint2";
		//from("timer:routingSlip?period=10000")
		//.transform().constant("Teste")
		//.routingSlip(simple(routingSlip))
		//;
		//from("direct:endpoint1").to("log:directendpoint1");
		//from("direct:endpoint2").to("log:directendpoint2");
		
		
		
		// Dynamic routing list Pattern
		/*
		 * Redirecting a test message to three endpoints at the same time based on your logic
		 * */
		
		// Demonstra toda o processo de roteamento que está sendo feita através dos logs
		//getContext().setTracing(true);
		
		// Captura todas as mensagens que não tenham sido processadas e envia para outra fila
		//errorHandler(deadLetterChannel("activemq:dead-letter-queue"));
		
		from("timer:routingSlip?period=10000")
		.transform().constant("Teste")
		.dynamicRouter(method("dynamicRouterBean"));
		
		from("direct:endpoint1")
		.wireTap("direct:wire-tap") // lets you forward messages to a separate location 
		.to("log:directendpoint1");
		
		from("direct:wire-tap")
	    .delay(1000).setBody().constant("Tapped")
	    .to("log:tap");
		
		from("direct:endpoint2").to("log:directendpoint2");
		from("direct:endpoint3").to("log:directendpoint3");
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

@Component
class DynamicRouterBean {
	
	Logger logger = LoggerFactory.getLogger(DynamicRouterBean.class);
	
	int invocations = 0;
	
	public String decideTheNextEndpoint(
		@ExchangeProperties Map<String, String> properties,
		@Headers Map<String, String> headers,
		@Body String body
	) {
		logger.info("{} {} {}", properties, headers, body);
		
		++invocations;
		
		if(invocations % 3 == 0) {
			return "direct:endpoint1";
		}else if(invocations % 3 == 1) {
			return "direct:endpoint2,direct:endpoint3";
		}
			
		return null;
    }
}