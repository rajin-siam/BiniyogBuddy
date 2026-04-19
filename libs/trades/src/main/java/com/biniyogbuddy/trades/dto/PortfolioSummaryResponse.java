package com.biniyogbuddy.trades.dto;

import java.math.BigDecimal;

public record PortfolioSummaryResponse(
        BigDecimal totalInvested,
        Long totalHoldings
) {}
