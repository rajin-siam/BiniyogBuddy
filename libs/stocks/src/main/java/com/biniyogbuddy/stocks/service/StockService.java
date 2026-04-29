package com.biniyogbuddy.stocks.service;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.exception.ResourceNotFoundException;
import com.biniyogbuddy.stocks.dto.StockRequest;
import com.biniyogbuddy.stocks.dto.StockResponse;
import com.biniyogbuddy.stocks.entity.Stock;
import com.biniyogbuddy.stocks.entity.StockStatus;
import com.biniyogbuddy.stocks.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final MessageResource messageResource;

    @Transactional(readOnly = true)
    public List<StockResponse> getAll() {
        return stockRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StockResponse getById(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.not.found", id)));
        return toResponse(stock);
    }

    @Transactional
    public StockResponse create(StockRequest request) {
        Stock stock = Stock.builder()
                .companyName(request.companyName())
                .tradingCode(request.tradingCode())
                .status(StockStatus.LISTED)
                .build();
        return toResponse(stockRepository.save(stock));
    }

    @Transactional
    public StockResponse update(Long id, StockRequest request) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.not.found", id)));
        stock.setCompanyName(request.companyName());
        stock.setTradingCode(request.tradingCode());
        return toResponse(stockRepository.save(stock));
    }

    @Transactional
    public void delete(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.not.found", id)));
        stock.setStatus(StockStatus.DELISTED);
        stockRepository.save(stock);
    }

    private StockResponse toResponse(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getTradingCode(),
                stock.getCompanyName(),
                stock.getShortName(),
                stock.getSector(),
                stock.getStatus(),
                stock.getCreatedAt(),
                stock.getUpdatedAt()
        );
    }
}
