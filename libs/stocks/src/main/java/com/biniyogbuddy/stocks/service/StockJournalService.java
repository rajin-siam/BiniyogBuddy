package com.biniyogbuddy.stocks.service;

import com.biniyogbuddy.common.exception.ResourceNotFoundException;
import com.biniyogbuddy.stocks.dto.StockJournalRequest;
import com.biniyogbuddy.stocks.dto.StockJournalResponse;
import com.biniyogbuddy.stocks.entity.StockJournal;
import com.biniyogbuddy.stocks.repository.StockJournalRepository;
import com.biniyogbuddy.users.entity.User;
import com.biniyogbuddy.users.repository.UserRepository;
import com.biniyogbuddy.common.config.MessageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockJournalService {

    private final StockJournalRepository stockJournalRepository;
    private final UserRepository userRepository;
    private final MessageResource messageResource;

    @Transactional(readOnly = true)
    public List<StockJournalResponse> getAllForCurrentUser() {
        User user = getCurrentUser();
        return stockJournalRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StockJournalResponse create(StockJournalRequest request) {
        User user = getCurrentUser();

        StockJournal entry = StockJournal.builder()
                .user(user)
                .stockName(request.stockName())
                .dseCode(request.dseCode())
                .cseCode(request.cseCode())
                .sector(request.sector())
                .purchasePrice(request.purchasePrice())
                .quantity(request.quantity())
                .tradeType(request.tradeType())
                .build();

        return toResponse(stockJournalRepository.save(entry));
    }

    @Transactional
    public StockJournalResponse update(Long id, StockJournalRequest request) {
        User user = getCurrentUser();
        StockJournal entry = findByIdAndUser(id, user.getId());

        entry.setStockName(request.stockName());
        entry.setDseCode(request.dseCode());
        entry.setCseCode(request.cseCode());
        entry.setSector(request.sector());
        entry.setPurchasePrice(request.purchasePrice());
        entry.setQuantity(request.quantity());
        entry.setTradeType(request.tradeType());

        return toResponse(stockJournalRepository.save(entry));
    }

    @Transactional
    public void delete(Long id) {
        User user = getCurrentUser();
        StockJournal entry = findByIdAndUser(id, user.getId());
        entry.setDeleted(true);
        stockJournalRepository.save(entry);
    }

    private StockJournal findByIdAndUser(Long id, Long userId) {
        return stockJournalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.journal.error.not.found", id)
                ));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.journal.error.user.not.found")
                ));
    }

    private StockJournalResponse toResponse(StockJournal entry) {
        return new StockJournalResponse(
                entry.getId(),
                entry.getStockName(),
                entry.getDseCode(),
                entry.getCseCode(),
                entry.getSector(),
                entry.getPurchasePrice(),
                entry.getQuantity(),
                entry.getTradeType(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }
}
