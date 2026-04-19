package com.biniyogbuddy.stocks.service;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.exception.ResourceNotFoundException;
import com.biniyogbuddy.stocks.dto.StockPriceRequest;
import com.biniyogbuddy.stocks.dto.StockPriceResponse;
import com.biniyogbuddy.stocks.entity.Stock;
import com.biniyogbuddy.stocks.entity.StockPrice;
import com.biniyogbuddy.stocks.repository.StockPriceRepository;
import com.biniyogbuddy.stocks.repository.StockRepository;
import com.biniyogbuddy.users.entity.User;
import com.biniyogbuddy.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockPriceService {

    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final MessageResource messageResource;

    @Transactional(readOnly = true)
    public List<StockPriceResponse> getHistory(Long stockId, LocalDate from, LocalDate to) {
        User user = getCurrentUser();
        findStockOwnedByUser(stockId, user.getId());

        List<StockPrice> prices = (from != null && to != null)
                ? stockPriceRepository.findAllByStockIdAndUserIdAndPriceDateBetween(stockId, user.getId(), from, to)
                : stockPriceRepository.findAllByStockIdAndUserId(stockId, user.getId());

        return prices.stream().map(this::toResponse).toList();
    }

    @Transactional
    public StockPriceResponse create(Long stockId, StockPriceRequest request) {
        User user = getCurrentUser();
        Stock stock = findStockOwnedByUser(stockId, user.getId());

        StockPrice stockPrice = StockPrice.builder()
                .stock(stock)
                .user(user)
                .price(request.price())
                .priceDate(request.priceDate())
                .build();

        return toResponse(stockPriceRepository.save(stockPrice));
    }

    @Transactional
    public void delete(Long stockId, Long priceId) {
        User user = getCurrentUser();
        findStockOwnedByUser(stockId, user.getId());

        StockPrice stockPrice = stockPriceRepository.findByIdAndUserId(priceId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.price.error.not.found", priceId)));

        stockPrice.setDeleted(true);
        stockPriceRepository.save(stockPrice);
    }

    private Stock findStockOwnedByUser(Long stockId, Long userId) {
        return stockRepository.findByIdAndUserId(stockId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.not.found", stockId)));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.user.not.found")));
    }

    private StockPriceResponse toResponse(StockPrice sp) {
        return new StockPriceResponse(
                sp.getId(),
                sp.getStock().getId(),
                sp.getStock().getStockName(),
                sp.getStock().getDseCode(),
                sp.getPrice(),
                sp.getPriceDate(),
                sp.getCreatedAt()
        );
    }
}
