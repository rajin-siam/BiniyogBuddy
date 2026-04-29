package com.biniyogbuddy.scraper.entity;


import com.biniyogbuddy.scraper.entity.ScraperStatus;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "scraper_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScraperLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScraperStatus status;

    @Column(name = "stocks_scraped")
    private Integer stocksScraped;

    @Column(name = "stocks_failed")
    private Integer stocksFailed;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "ran_at", nullable = false)
    private LocalDateTime ranAt;
}