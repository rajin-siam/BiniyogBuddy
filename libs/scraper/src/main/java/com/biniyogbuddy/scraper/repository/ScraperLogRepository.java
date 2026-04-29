package com.biniyogbuddy.scraper.repository;


import com.biniyogbuddy.scraper.entity.ScraperLog;
import com.biniyogbuddy.scraper.entity.ScraperStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScraperLogRepository extends JpaRepository<ScraperLog, Long> {

    Optional<ScraperLog> findTopByOrderByRanAtDesc();

    List<ScraperLog> findTop5ByOrderByRanAtDesc();

    long countByStatusAndRanAtAfter(ScraperStatus status, LocalDateTime since);

    List<ScraperLog> findByRanAtBetweenOrderByRanAtDesc(
            LocalDateTime from, LocalDateTime to);
}