package com.biniyogbuddy.stocks.service;

import com.biniyogbuddy.stocks.dto.SectorResponse;
import com.biniyogbuddy.stocks.dto.SectorStockMappingRequest;
import com.biniyogbuddy.stocks.entity.Sector;
import com.biniyogbuddy.stocks.entity.Stock;
import com.biniyogbuddy.stocks.entity.StockStatus;
import com.biniyogbuddy.stocks.repository.SectorRepository;
import com.biniyogbuddy.stocks.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public List<SectorResponse> getAll() {
        return sectorRepository.findAll()
                .stream()
                .map(s -> new SectorResponse(s.getId(), s.getName(), s.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void ingestSectorMappings(List<SectorStockMappingRequest> mappings) {
        mappings.forEach(m -> {
            Sector sector = sectorRepository.findByName(m.sectorName())
                    .orElseGet(() -> sectorRepository.save(Sector.builder().name(m.sectorName()).build()));
            m.tradingCodes().forEach(code -> {
                Stock stock = stockRepository.findByTradingCode(code)
                        .orElseGet(() -> Stock.builder().tradingCode(code).status(StockStatus.NOT_VERIFIED).build());
                stock.setSector(sector);
                stockRepository.save(stock);
            });
        });
    }
}
