package com.biniyogbuddy.stocks.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockPriceRequest(
        @NotNull
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @NotNull
        LocalDate priceDate
) {}
