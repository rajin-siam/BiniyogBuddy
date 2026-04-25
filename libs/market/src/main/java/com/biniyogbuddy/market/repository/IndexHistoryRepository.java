package com.biniyogbuddy.market.repository;

import com.biniyogbuddy.market.entity.IndexHistory;
import com.biniyogbuddy.market.entity.IndexName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IndexHistoryRepository extends JpaRepository<IndexHistory, Long> {

    List<IndexHistory> findByIndexNameAndTradeDateBetweenOrderByTradeDateAsc(
            IndexName indexName,
            LocalDate from,
            LocalDate to
    );

    Optional<IndexHistory> findTopByIndexNameOrderByTradeDateDesc(IndexName indexName);

    Optional<IndexHistory> findByIndexNameAndTradeDate(IndexName indexName, LocalDate tradeDate);
}