package com.biniyogbuddy.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class ScrapedPayload {

    private List<StockRowDto> stockRows;
    private List<IndexDataDto> indexData;
    private MarketStatsDto marketStats;
    private String marketStatus;

    @Data
    public static class StockRowDto {
        private String tradingCode;
        private String ltp;
        private String high;
        private String low;
        private String closePrice;
        private String yesterdayClose;
        private String change;
        private String trades;
        private String valueMn;
        private String volume;
    }

    @Data
    public static class IndexDataDto {
        private String name;
        private String value;
        private String change;
        private String changePct;
    }

    @Data
    public static class MarketStatsDto {
        private String totalTrade;
        private String totalVolume;
        private String totalValueMn;
        private String advances;
        private String declines;
        private String unchanged;
    }
}
