package com.in28minutes.microsservices.camelmicrosservicea.dto;

import java.math.BigDecimal;

public record CurrencyExchangeDto(Long id, String from, String to, BigDecimal conversionMultiple) {}
