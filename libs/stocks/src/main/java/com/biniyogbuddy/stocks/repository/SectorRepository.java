package com.biniyogbuddy.stocks.repository;

import com.biniyogbuddy.stocks.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {
    boolean existsByName(String name);
    Optional<Sector> findByName(String name);
}
