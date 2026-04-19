package com.biniyogbuddy.stocks.entity;

import com.biniyogbuddy.common.entity.BaseEntity;
import com.biniyogbuddy.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_prices", indexes = {
        @Index(name = "idx_stock_prices_stock_id", columnList = "stock_id"),
        @Index(name = "idx_stock_prices_user_id", columnList = "user_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockPrice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_price_seq_gen")
    @SequenceGenerator(name = "stock_price_seq_gen", sequenceName = "stock_price_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "price_date", nullable = false)
    private LocalDate priceDate;
}
