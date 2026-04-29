package com.biniyogbuddy.trades.dto;

import java.math.BigDecimal;

public record HoldingResponse(
        Long stockJournalId,
        String companyName,
        String tradingCode,
        Integer netQuantity,
        BigDecimal avgBuyPrice,
        BigDecimal totalInvested
) {}
