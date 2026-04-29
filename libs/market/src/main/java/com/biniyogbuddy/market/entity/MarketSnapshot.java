package com.biniyogbuddy.market.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── DSEX (DSE Broad Index) ─────────────────────────────────────────
    @Column(name = "dsex_value", precision = 12, scale = 4)
    private BigDecimal dsexValue;

    @Column(name = "dsex_change", precision = 10, scale = 4)
    private BigDecimal dsexChange;

    @Column(name = "dsex_change_pct", precision = 8, scale = 4)
    private BigDecimal dsexChangePct;

    // ── DSES (DSE Shariah Index) ───────────────────────────────────────
    @Column(name = "dses_value", precision = 12, scale = 4)
    private BigDecimal dsesValue;

    @Column(name = "dses_change", precision = 10, scale = 4)
    private BigDecimal dsesChange;

    @Column(name = "dses_change_pct", precision = 8, scale = 4)
    private BigDecimal dsesChangePct;

    // ── DS30 (DSE 30 Blue Chip Index) ─────────────────────────────────
    @Column(name = "ds30_value", precision = 12, scale = 4)
    private BigDecimal ds30Value;

    @Column(name = "ds30_change", precision = 10, scale = 4)
    private BigDecimal ds30Change;

    @Column(name = "ds30_change_pct", precision = 8, scale = 4)
    private BigDecimal ds30ChangePct;

    // ── Market status ──────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "market_status", nullable = false, length = 20)
    private MarketStatus marketStatus;

    // ── Exchange-wide stats ────────────────────────────────────────────
    @Column(name = "total_turnover_mn", precision = 18, scale = 4)
    private BigDecimal totalTurnoverMn;

    @Column(name = "total_trades")
    private Integer totalTrades;

    @Column(name = "total_volume")
    private Long totalVolume;

    private Integer advances;

    private Integer declines;

    private Integer unchanged;

    // ── Timestamps ────────────────────────────────────────────────────
    @Column(name = "trade_date")
    private LocalDate tradeDate;
    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;
}