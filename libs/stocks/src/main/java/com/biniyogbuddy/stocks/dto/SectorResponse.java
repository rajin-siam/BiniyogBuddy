package com.biniyogbuddy.stocks.dto;

import java.time.LocalDateTime;

public record SectorResponse(
        Long id,
        String name,
        LocalDateTime createdAt
) {}
