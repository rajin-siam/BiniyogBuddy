package com.biniyogbuddy.stocks.dto;

import com.biniyogbuddy.stocks.entity.Sector;
import com.biniyogbuddy.stocks.entity.TradeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockJournalResponse(
        Long id,
        String stockName,
        String dseCode,
        String cseCode,
        Sector sector,
        BigDecimal purchasePrice,
        Integer quantity,
        TradeType tradeType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
