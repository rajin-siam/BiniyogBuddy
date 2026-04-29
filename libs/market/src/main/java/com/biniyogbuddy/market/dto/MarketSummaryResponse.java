package com.biniyogbuddy.market.dto;

import com.biniyogbuddy.market.entity.MarketStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MarketSummaryResponse(
        BigDecimal dsexValue,
        BigDecimal dsexChange,
        BigDecimal dsexChangePct,
        BigDecimal dsesValue,
        BigDecimal dsesChange,
        BigDecimal dsesChangePct,
        BigDecimal ds30Value,
        BigDecimal ds30Change,
        BigDecimal ds30ChangePct,
        MarketStatus marketStatus,
        BigDecimal totalTurnoverMn,
        Integer totalTrades,
        Long totalVolume,
        Integer advances,
        Integer declines,
        Integer unchanged,
        LocalDate tradeDate,
        LocalDateTime fetchedAt
) {}
