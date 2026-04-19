package com.biniyogbuddy.trades.dto;

import java.math.BigDecimal;

public record HoldingResponse(
        Long stockJournalId,
        String stockName,
        String dseCode,
        Integer netQuantity,
        BigDecimal avgBuyPrice,
        BigDecimal totalInvested
) {}
