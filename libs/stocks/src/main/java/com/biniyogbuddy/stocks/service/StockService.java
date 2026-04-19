package com.biniyogbuddy.stocks.service;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.exception.ResourceNotFoundException;
import com.biniyogbuddy.stocks.dto.StockRequest;
import com.biniyogbuddy.stocks.dto.StockResponse;
import com.biniyogbuddy.stocks.entity.Stock;
import com.biniyogbuddy.stocks.repository.StockRepository;
import com.biniyogbuddy.users.entity.User;
import com.biniyogbuddy.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final MessageResource messageResource;

    @Transactional(readOnly = true)
    public List<StockResponse> getAllForCurrentUser() {
        User user = getCurrentUser();
        return stockRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StockResponse create(StockRequest request) {
        User user = getCurrentUser();
        Stock stock = Stock.builder()
                .user(user)
                .stockName(request.stockName())
                .dseCode(request.dseCode())
                .cseCode(request.cseCode())
                .sector(request.sector())
                .build();
        return toResponse(stockRepository.save(stock));
    }

    @Transactional
    public StockResponse update(Long id, StockRequest request) {
        User user = getCurrentUser();
        Stock stock = findByIdAndUser(id, user.getId());
        stock.setStockName(request.stockName());
        stock.setDseCode(request.dseCode());
        stock.setCseCode(request.cseCode());
        stock.setSector(request.sector());
        return toResponse(stockRepository.save(stock));
    }

    @Transactional
    public void delete(Long id) {
        User user = getCurrentUser();
        Stock stock = findByIdAndUser(id, user.getId());
        stock.setDeleted(true);
        stockRepository.save(stock);
    }

    private Stock findByIdAndUser(Long id, Long userId) {
        return stockRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.not.found", id)));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.user.not.found")));
    }

    private StockResponse toResponse(Stock stock) {
        return new StockResponse(
                stock.getId(),
                stock.getStockName(),
                stock.getDseCode(),
                stock.getCseCode(),
                stock.getSector(),
                stock.getCreatedAt(),
                stock.getUpdatedAt()
        );
    }
}
