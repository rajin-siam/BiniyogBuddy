package com.biniyogbuddy.market.repository;


import com.biniyogbuddy.market.entity.MarketSnapshot;
import com.biniyogbuddy.market.entity.MarketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MarketSnapshotRepository extends JpaRepository<MarketSnapshot, Long> {

    Optional<MarketSnapshot> findTopByOrderByFetchedAtDesc();

    @Query("SELECT m.marketStatus FROM MarketSnapshot m ORDER BY m.fetchedAt DESC LIMIT 1")
    Optional<MarketStatus> findLatestMarketStatus();

    Optional<MarketSnapshot> findTopByTradeDateOrderByFetchedAtDesc(LocalDate tradeDate);
}