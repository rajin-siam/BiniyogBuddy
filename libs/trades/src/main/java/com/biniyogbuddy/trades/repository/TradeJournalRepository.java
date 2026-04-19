package com.biniyogbuddy.trades.repository;

import com.biniyogbuddy.trades.entity.TradeJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeJournalRepository extends JpaRepository<TradeJournal, Long> {

    @Query("SELECT t FROM TradeJournal t JOIN FETCH t.stock WHERE t.user.id = :userId ORDER BY t.tradeDate DESC")
    List<TradeJournal> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM TradeJournal t JOIN FETCH t.stock WHERE t.stock.id = :stockId AND t.user.id = :userId")
    List<TradeJournal> findAllByStockIdAndUserId(@Param("stockId") Long stockId, @Param("userId") Long userId);

    Optional<TradeJournal> findByIdAndUserId(Long id, Long userId);
}
