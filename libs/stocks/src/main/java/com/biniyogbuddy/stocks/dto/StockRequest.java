package com.biniyogbuddy.stocks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StockRequest(
        @NotBlank
        @Size(max = 100)
        String companyName,

        @NotBlank
        @Pattern(regexp = "^[A-Z0-9]{1,12}$", message = "Trading code must be 1-12 uppercase letters or digits")
        String tradingCode
) {}
