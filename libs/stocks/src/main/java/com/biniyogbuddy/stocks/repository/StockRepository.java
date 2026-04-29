package com.biniyogbuddy.stocks.repository;

import com.biniyogbuddy.stocks.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByTradingCode(String tradingCode);

    boolean existsByTradingCode(String tradingCode);
}
