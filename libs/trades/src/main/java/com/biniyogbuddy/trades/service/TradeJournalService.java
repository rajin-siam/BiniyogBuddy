package com.biniyogbuddy.trades.service;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.exception.InsufficientSharesException;
import com.biniyogbuddy.common.exception.ResourceNotFoundException;
import com.biniyogbuddy.stocks.entity.Stock;
import com.biniyogbuddy.stocks.repository.StockRepository;
import com.biniyogbuddy.trades.dto.TradeJournalRequest;
import com.biniyogbuddy.trades.dto.TradeJournalResponse;
import com.biniyogbuddy.trades.entity.TradeDirection;
import com.biniyogbuddy.trades.entity.TradeJournal;
import com.biniyogbuddy.trades.repository.TradeJournalRepository;
import com.biniyogbuddy.users.entity.User;
import com.biniyogbuddy.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeJournalService {

    private final TradeJournalRepository tradeJournalRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final MessageResource messageResource;

    @Transactional(readOnly = true)
    public List<TradeJournalResponse> getAll(Long stockId, TradeDirection tradeDirection, LocalDate tradeDate, String sortBy, String sortDir) {
        User user = getCurrentUser();
        List<TradeJournal> trades = tradeJournalRepository.findAllByUserId(user.getId());

        return trades.stream()
                .filter(t -> stockId == null || t.getStock().getId().equals(stockId))
                .filter(t -> tradeDirection == null || t.getTradeDirection() == tradeDirection)
                .filter(t -> tradeDate == null || t.getTradeDate().equals(tradeDate))
                .sorted(buildComparator(sortBy, sortDir))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TradeJournalResponse create(TradeJournalRequest request) {
        User user = getCurrentUser();
        Stock stock = findStockOwnedByUser(request.stockId(), user.getId());

        if (request.tradeDirection() == TradeDirection.SELL) {
            int netHeld = calculateNetHeld(request.stockId(), user.getId(), null);
            if (request.quantity() > netHeld) {
                throw new InsufficientSharesException(
                        messageResource.getMessage("trade.journal.error.insufficient.shares", request.quantity(), netHeld));
            }
        }

        TradeJournal trade = TradeJournal.builder()
                .user(user)
                .stock(stock)
                .tradeDirection(request.tradeDirection())
                .tradeType(request.tradeType())
                .tradeDate(request.tradeDate())
                .pricePerShare(request.pricePerShare())
                .quantity(request.quantity())
                .totalValue(request.pricePerShare().multiply(BigDecimal.valueOf(request.quantity())))
                .noteWhy(request.noteWhy())
                .noteLearned(request.noteLearned())
                .build();

        return toResponse(tradeJournalRepository.save(trade));
    }

    @Transactional(readOnly = true)
    public TradeJournalResponse getById(Long id) {
        User user = getCurrentUser();
        return toResponse(findByIdAndUser(id, user.getId()));
    }

    @Transactional
    public TradeJournalResponse update(Long id, TradeJournalRequest request) {
        User user = getCurrentUser();
        TradeJournal trade = findByIdAndUser(id, user.getId());
        Stock stock = findStockOwnedByUser(request.stockId(), user.getId());

        if (request.tradeDirection() == TradeDirection.SELL) {
            int netHeld = calculateNetHeld(request.stockId(), user.getId(), id);
            if (request.quantity() > netHeld) {
                throw new InsufficientSharesException(
                        messageResource.getMessage("trade.journal.error.insufficient.shares", request.quantity(), netHeld));
            }
        }

        trade.setStock(stock);
        trade.setTradeDirection(request.tradeDirection());
        trade.setTradeType(request.tradeType());
        trade.setTradeDate(request.tradeDate());
        trade.setPricePerShare(request.pricePerShare());
        trade.setQuantity(request.quantity());
        trade.setTotalValue(request.pricePerShare().multiply(BigDecimal.valueOf(request.quantity())));
        trade.setNoteWhy(request.noteWhy());
        trade.setNoteLearned(request.noteLearned());

        return toResponse(tradeJournalRepository.save(trade));
    }

    @Transactional
    public void delete(Long id) {
        User user = getCurrentUser();
        TradeJournal trade = findByIdAndUser(id, user.getId());
        trade.setDeleted(true);
        tradeJournalRepository.save(trade);
    }

    private int calculateNetHeld(Long stockId, Long userId, Long excludeTradeId) {
        return tradeJournalRepository.findAllByStockIdAndUserId(stockId, userId)
                .stream()
                .filter(t -> excludeTradeId == null || !t.getId().equals(excludeTradeId))
                .mapToInt(t -> t.getTradeDirection() == TradeDirection.BUY ? t.getQuantity() : -t.getQuantity())
                .sum();
    }

    private Stock findStockOwnedByUser(Long stockId, Long userId) {
        return stockRepository.findByIdAndUserId(stockId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.not.found", stockId)));
    }

    private TradeJournal findByIdAndUser(Long id, Long userId) {
        return tradeJournalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("trade.journal.error.not.found", id)));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.user.not.found")));
    }

    private Comparator<TradeJournal> buildComparator(String sortBy, String sortDir) {
        Comparator<TradeJournal> comparator = switch (sortBy != null ? sortBy.toLowerCase() : "date") {
            case "stockcode" -> Comparator.comparing(t -> t.getStock().getDseCode());
            case "tradetype" -> Comparator.comparing(t -> t.getTradeDirection().name());
            default -> Comparator.comparing(TradeJournal::getTradeDate);
        };
        return "asc".equalsIgnoreCase(sortDir) ? comparator : comparator.reversed();
    }

    private TradeJournalResponse toResponse(TradeJournal trade) {
        return new TradeJournalResponse(
                trade.getId(),
                trade.getStock().getId(),
                trade.getStock().getStockName(),
                trade.getStock().getDseCode(),
                trade.getTradeDirection(),
                trade.getTradeType(),
                trade.getTradeDate(),
                trade.getPricePerShare(),
                trade.getQuantity(),
                trade.getTotalValue(),
                trade.getNoteWhy(),
                trade.getNoteLearned(),
                trade.getCreatedAt(),
                trade.getUpdatedAt()
        );
    }
}
