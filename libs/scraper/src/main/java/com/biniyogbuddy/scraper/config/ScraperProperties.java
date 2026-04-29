package com.biniyogbuddy.scraper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "scraper")
public class ScraperProperties {

    /** Live stock price table — all listed stocks */
    private String priceUrl = "https://dsebd.org/latest_share_price_scroll_l.php";

    /** Market homepage — DSEX/DSES/DS30 index values + total trade/volume/value + advances/declines */
    private String indexUrl = "https://dsebd.org/index.php";

    private int    timeoutMs      = 20_000;
    private String userAgent      = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36";
    private int    retryAttempts  = 3;
    private long   retryDelayMs   = 2_000;

    private String cronMarketHours = "0 0 * * * *";
    private String cronEndOfDay    = "0 35 8 * * SUN-THU";
    private String cronWeeklySync  = "0 0 20 * * FRI";
}