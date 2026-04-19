package com.biniyogbuddy.trades.entity;

import com.biniyogbuddy.common.entity.BaseEntity;
import com.biniyogbuddy.stocks.entity.Stock;
import com.biniyogbuddy.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trade_journals", indexes = {
        @Index(name = "idx_trade_journals_user_id", columnList = "user_id"),
        @Index(name = "idx_trade_journals_stock_id", columnList = "stock_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TradeJournal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trade_journal_seq_gen")
    @SequenceGenerator(name = "trade_journal_seq_gen", sequenceName = "trade_journal_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_direction", nullable = false, length = 4)
    private TradeDirection tradeDirection;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type", nullable = false)
    private TradeType tradeType;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "price_per_share", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerShare;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_value", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalValue;

    @Column(name = "note_why", columnDefinition = "TEXT")
    private String noteWhy;

    @Column(name = "note_learned", columnDefinition = "TEXT")
    private String noteLearned;
}
