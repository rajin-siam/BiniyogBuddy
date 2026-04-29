package com.biniyogbuddy.market.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "index_history",
        uniqueConstraints = @UniqueConstraint(
                name  = "uq_index_name_trade_date",
                columnNames = {"index_name", "trade_date"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "index_name", nullable = false, length = 10)
    private IndexName indexName;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal close;

    @Column(precision = 12, scale = 4)
    private BigDecimal change;

    @Column(name = "change_pct", precision = 8, scale = 4)
    private BigDecimal changePct;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}