package com.biniyogbuddy.stocks.dto;

import com.biniyogbuddy.stocks.entity.Sector;
import com.biniyogbuddy.stocks.entity.TradeType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record StockJournalRequest(
        @NotBlank
        @Size(max = 100)
        String stockName,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{1,12}$", message = "DSE code must be 1-12 uppercase letters")
        String dseCode,

        @Pattern(regexp = "^[A-Z]{1,12}$", message = "CSE code must be 1-12 uppercase letters")
        String cseCode,

        @NotNull
        Sector sector,

        @NotNull
        @DecimalMin(value = "0.01", message = "Purchase price must be greater than 0")
        @Digits(integer = 10, fraction = 2)
        BigDecimal purchasePrice,

        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull
        TradeType tradeType
) {}
