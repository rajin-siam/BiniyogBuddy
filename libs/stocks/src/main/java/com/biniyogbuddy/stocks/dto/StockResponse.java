package com.biniyogbuddy.stocks.dto;

import com.biniyogbuddy.stocks.entity.Sector;
import com.biniyogbuddy.stocks.entity.StockStatus;

import java.time.LocalDateTime;

public record StockResponse(
        Long id,
        String tradingCode,
        String companyName,
        String shortName,
        Sector sector,
        StockStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
