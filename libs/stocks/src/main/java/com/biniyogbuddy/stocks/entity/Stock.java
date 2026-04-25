package com.biniyogbuddy.stocks.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_seq_gen")
    @SequenceGenerator(name = "stock_seq_gen", sequenceName = "stock_seq", allocationSize = 1)
    private Long id;

    @Column(name = "trading_code", unique = true, nullable = false)
    private String tradingCode;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "short_name")
    private String shortName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @Column(length = 2)
    private String category;

    @Column(name = "listing_date")
    private LocalDate listingDate;

    private String website;

    @Column(name = "total_shares")
    private Long totalShares;

    @Column(name = "paid_up_capital_mn", precision = 18, scale = 4)
    private BigDecimal paidUpCapitalMn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockStatus status = StockStatus.LISTED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}