package com.biniyogbuddy.stocks.entity;

import com.biniyogbuddy.common.entity.BaseEntity;
import com.biniyogbuddy.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "stocks", indexes = {
        @Index(name = "idx_stocks_user_id", columnList = "user_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_seq_gen")
    @SequenceGenerator(name = "stock_seq_gen", sequenceName = "stock_seq", allocationSize = 1)
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
}
