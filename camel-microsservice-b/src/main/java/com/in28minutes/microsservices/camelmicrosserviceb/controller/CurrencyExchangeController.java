package com.in28minutes.microsservices.camelmicrosserviceb.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in28minutes.microsservices.camelmicrosserviceb.routes.CurrencyExchange;

@RestController
@RequestMapping("currency-exchange")
public class CurrencyExchangeController {
	
	@GetMapping(
		value = "from/{from}/to/{to}",
		produces = "application/json"
	)
	public ResponseEntity<CurrencyExchange> findConversionValue(
		@PathVariable String from, 
		@PathVariable String to
	) {
		CurrencyExchange currencyExchange = new CurrencyExchange(
			1001L, 
			from, 
			to, BigDecimal.TEN
		); 
		
		return ResponseEntity.ok(currencyExchange);
	}
}
