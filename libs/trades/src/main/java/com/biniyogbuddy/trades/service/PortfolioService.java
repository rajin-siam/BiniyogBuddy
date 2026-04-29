package com.biniyogbuddy.trades.service;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.exception.ResourceNotFoundException;
import com.biniyogbuddy.trades.dto.HoldingResponse;
import com.biniyogbuddy.trades.dto.PortfolioSummaryResponse;
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
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final TradeJournalRepository tradeJournalRepository;
    private final UserRepository userRepository;
    private final MessageResource messageResource;

    @Transactional(readOnly = true)
    public PortfolioSummaryResponse getSummary() {
        User user = getCurrentUser();
        List<TradeJournal> trades = tradeJournalRepository.findAllByUserId(user.getId());

        BigDecimal totalInvested = trades.stream()
                .filter(t -> t.getTradeDirection() == TradeDirection.BUY)
                .map(TradeJournal::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalHoldings = groupByStock(trades).values().stream()
                .filter(stockTrades -> netQuantity(stockTrades) > 0)
                .count();

        return new PortfolioSummaryResponse(totalInvested, totalHoldings);
    }

    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldings() {
        User user = getCurrentUser();
        List<TradeJournal> trades = tradeJournalRepository.findAllByUserId(user.getId());

        return groupByStock(trades).values().stream()
                .map(stockTrades -> {
                    int net = netQuantity(stockTrades);
                    if (net <= 0) return null;

                    var stock = stockTrades.get(0).getStock();

                    BigDecimal totalBuyValue = stockTrades.stream()
                            .filter(t -> t.getTradeDirection() == TradeDirection.BUY)
                            .map(TradeJournal::getTotalValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    int totalBuyQty = stockTrades.stream()
                            .filter(t -> t.getTradeDirection() == TradeDirection.BUY)
                            .mapToInt(TradeJournal::getQuantity)
                            .sum();

                    BigDecimal avgBuyPrice = totalBuyQty > 0
                            ? totalBuyValue.divide(BigDecimal.valueOf(totalBuyQty), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return new HoldingResponse(
                            stock.getId(),
                            stock.getCompanyName(),
                            stock.getTradingCode(),
                            net,
                            avgBuyPrice,
                            totalBuyValue
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Map<Long, List<TradeJournal>> groupByStock(List<TradeJournal> trades) {
        return trades.stream()
                .collect(Collectors.groupingBy(t -> t.getStock().getId()));
    }

    private int netQuantity(List<TradeJournal> trades) {
        return trades.stream()
                .mapToInt(t -> t.getTradeDirection() == TradeDirection.BUY ? t.getQuantity() : -t.getQuantity())
                .sum();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageResource.getMessage("stock.error.user.not.found")));
    }
}
