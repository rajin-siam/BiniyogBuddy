package com.biniyogbuddy.stocks.dto;

import com.biniyogbuddy.stocks.entity.Sector;

import java.time.LocalDateTime;

public record StockResponse(
        Long id,
        String stockName,
        String dseCode,
        String cseCode,
        Sector sector,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
