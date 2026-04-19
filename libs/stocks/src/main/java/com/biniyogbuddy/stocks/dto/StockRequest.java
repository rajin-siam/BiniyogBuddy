package com.biniyogbuddy.stocks.dto;

import com.biniyogbuddy.stocks.entity.Sector;
import jakarta.validation.constraints.*;

public record StockRequest(
        @NotBlank
        @Size(max = 100)
        String stockName,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{1,12}$", message = "DSE code must be 1-12 uppercase letters")
        String dseCode,

        @Pattern(regexp = "^[A-Z]{1,12}$", message = "CSE code must be 1-12 uppercase letters")
        String cseCode,

        @NotNull
        Sector sector
) {}
