package com.biniyogbuddy.stocks.repository;

import com.biniyogbuddy.stocks.entity.StockJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockJournalRepository extends JpaRepository<StockJournal, Long> {

    List<StockJournal> findAllByUserId(Long userId);

    Optional<StockJournal> findByIdAndUserId(Long id, Long userId);
}
