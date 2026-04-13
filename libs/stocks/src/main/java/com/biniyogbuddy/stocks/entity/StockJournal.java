package com.biniyogbuddy.stocks.entity;

import com.biniyogbuddy.common.entity.BaseEntity;
import com.biniyogbuddy.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_journal", indexes = {
        @Index(name = "idx_stock_journal_user_id", columnList = "user_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockJournal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_journal_seq_gen")
    @SequenceGenerator(name = "stock_journal_seq_gen", sequenceName = "stock_journal_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

    @Column(name = "dse_code", nullable = false, length = 12)
    private String dseCode;

    @Column(name = "cse_code", length = 12)
    private String cseCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sector sector;

    @Column(name = "purchase_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type", nullable = false)
    private TradeType tradeType;
}
