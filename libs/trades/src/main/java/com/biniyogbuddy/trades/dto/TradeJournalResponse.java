package com.biniyogbuddy.trades.dto;

import com.biniyogbuddy.trades.entity.TradeDirection;
import com.biniyogbuddy.trades.entity.TradeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TradeJournalResponse(
        Long id,
        Long stockId,
        String stockName,
        String dseCode,
        TradeDirection tradeDirection,
        TradeType tradeType,
        LocalDate tradeDate,
        BigDecimal pricePerShare,
        Integer quantity,
        BigDecimal totalValue,
        String noteWhy,
        String noteLearned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
