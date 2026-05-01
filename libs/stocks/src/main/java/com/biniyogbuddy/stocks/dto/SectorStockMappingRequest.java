package com.biniyogbuddy.stocks.dto;

import java.util.List;

public record SectorStockMappingRequest(
        String sectorName,
        List<String> tradingCodes
) {}
