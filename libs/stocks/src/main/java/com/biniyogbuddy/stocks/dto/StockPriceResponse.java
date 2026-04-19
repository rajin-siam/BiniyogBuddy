package com.biniyogbuddy.stocks.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockPriceResponse(
        Long id,
        Long stockId,
        String stockName,
        String dseCode,
        BigDecimal price,
        LocalDate priceDate,
        LocalDateTime createdAt
) {}
