package com.biniyogbuddy.stocks.service;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.exception.ResourceNotFoundException;
import com.biniyogbuddy.stocks.dto.StockPriceResponse;
import com.biniyogbuddy.stocks.entity.Stock;
import com.biniyogbuddy.stocks.entity.StockPrice;
import com.biniyogbuddy.stocks.repository.StockPriceRepository;
import com.biniyogbuddy.stocks.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockPriceService {

    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;
    private final MessageResource messageResource;

    @Transactional(readOnly = true)
    public StockPriceResponse getCurrentPrice(Long stockId) {
        stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.not.found", stockId)));

        StockPrice price = stockPriceRepository.findByStockId(stockId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.price.error.not.found", stockId)));

        return toResponse(price);
    }

    private StockPriceResponse toResponse(StockPrice sp) {
        Stock stock = sp.getStock();
        return new StockPriceResponse(
                sp.getId(),
                stock.getId(),
                stock.getTradingCode(),
                stock.getCompanyName(),
                sp.getLtp(),
                sp.getOpenPrice(),
                sp.getHigh(),
                sp.getLow(),
                sp.getClosePrice(),
                sp.getYesterdayClose(),
                sp.getChange(),
                sp.getChangePct(),
                sp.getVolume(),
                sp.getValueMn(),
                sp.getTrades(),
                sp.getFetchedAt()
        );
    }
}
