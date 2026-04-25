package com.biniyogbuddy.stocks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :stockId AND sp.user.id = :userId ORDER BY sp.priceDate DESC")
    List<StockPrice> findAllByStockIdAndUserId(@Param("stockId") Long stockId, @Param("userId") Long userId);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :stockId AND sp.user.id = :userId AND sp.priceDate BETWEEN :from AND :to ORDER BY sp.priceDate DESC")
    List<StockPrice> findAllByStockIdAndUserIdAndPriceDateBetween(
            @Param("stockId") Long stockId,
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    Optional<StockPrice> findByIdAndUserId(Long id, Long userId);
}
