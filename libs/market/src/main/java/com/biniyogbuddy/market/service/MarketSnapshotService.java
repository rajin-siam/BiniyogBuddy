package com.biniyogbuddy.market.service;

import com.biniyogbuddy.common.exception.ResourceNotFoundException;
import com.biniyogbuddy.market.dto.MarketStatusResponse;
import com.biniyogbuddy.market.dto.MarketSummaryResponse;
import com.biniyogbuddy.market.mapper.MarketSnapshotMapper;
import com.biniyogbuddy.market.repository.MarketSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketSnapshotService {

    private final MarketSnapshotRepository marketSnapshotRepository;
    private final MarketSnapshotMapper marketSnapshotMapper;

    @Transactional(readOnly = true)
    public MarketSummaryResponse getLatestSummary() {
        return marketSnapshotRepository.findTopByOrderByFetchedAtDesc()
                .map(marketSnapshotMapper::toSummaryResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No market data available yet"));
    }

    @Transactional(readOnly = true)
    public MarketStatusResponse findLatestMarketStatus() {
        return marketSnapshotRepository.findTopByOrderByFetchedAtDesc()
                .map(marketSnapshotMapper::toStatusResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No market status available yet"));
    }
}
