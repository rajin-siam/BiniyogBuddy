package com.biniyogbuddy.market.dto;

import java.time.LocalDateTime;

public record MarketStatusResponse(String status, LocalDateTime lastUpdatedOn) {
}
