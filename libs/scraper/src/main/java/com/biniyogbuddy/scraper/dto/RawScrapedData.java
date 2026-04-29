package com.biniyogbuddy.scraper.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Raw string data returned by DsePriceFetcher before any type conversion.
 * Everything here is a plain String — DseParser converts them to numbers later.
 */
public class RawScrapedData {

    /** One row from the latest_share_price_scroll_l.php table */
    @Data
    @Builder
    public static class RawStockRow {
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

    /** One index row from the index table on index.php */
    @Data
    @Builder
    public static class RawIndexData {
        private String name;       // "DSEX" / "DSES" / "DS30"
        private String value;      // "5316.18325"
        private String change;     // "17.59743"
        private String changePct;  // "0.33212%"
    }

    /**
     * Market-wide summary stats from index.php.
     *
     * Scraped from these two stat blocks:
     *   | Total Trade | Total Volume | Total Value in Taka (mn) |
     *   | Issues Advanced | Issues Declined | Issues Unchanged |
     */
    @Data
    @Builder
    public static class RawMarketStats {
        private String totalTrade;    // "253698"
        private String totalVolume;   // "312036419"
        private String totalValueMn;  // "9824.205"
        private String advances;      // "157"  — issues that went up
        private String declines;      // "172"  — issues that went down
        private String unchanged;     // "62"   — issues with zero change
    }

    /** Complete result of one full scrape run — both pages combined */
    @Data
    @Builder
    public static class ScrapedPage {
        private List<RawStockRow>  stockRows;
        private List<RawIndexData> indexData;
        private RawMarketStats     marketStats;   // NEW — from index.php
        private String             marketStatus;  // "open" / "closed" / "pre_open"
        private long               fetchDurationMs;
    }
}