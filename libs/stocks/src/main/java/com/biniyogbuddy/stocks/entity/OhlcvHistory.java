package com.biniyogbuddy.stocks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ohlcv_history",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ohlcv_stock_date",
                columnNames = {"stock_id", "trade_date"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OhlcvHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;

    @Column(name = "value_mn", precision = 18, scale = 4)
    private BigDecimal valueMn;

    private Integer trades;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}