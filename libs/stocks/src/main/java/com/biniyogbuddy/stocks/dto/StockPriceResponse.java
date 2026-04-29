package com.biniyogbuddy.stocks.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockPriceResponse(
        Long id,
        Long stockId,
        String tradingCode,
        String companyName,
        BigDecimal ltp,
        BigDecimal openPrice,
        BigDecimal high,
        BigDecimal low,
        BigDecimal closePrice,
        BigDecimal yesterdayClose,
        BigDecimal change,
        BigDecimal changePct,
        Long volume,
        BigDecimal valueMn,
        Integer trades,
        LocalDateTime fetchedAt
) {}
