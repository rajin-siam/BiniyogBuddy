package com.biniyogbuddy.trades.dto;

import com.biniyogbuddy.trades.entity.TradeDirection;
import com.biniyogbuddy.trades.entity.TradeType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TradeJournalRequest(
        @NotNull
        Long stockId,

        @NotNull
        TradeDirection tradeDirection,

        @NotNull
        TradeType tradeType,

        @NotNull
        LocalDate tradeDate,

        @NotNull
        @DecimalMin(value = "0.01", message = "Price per share must be greater than 0")
        @Digits(integer = 10, fraction = 2)
        BigDecimal pricePerShare,

        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        String noteWhy,

        String noteLearned
) {}
