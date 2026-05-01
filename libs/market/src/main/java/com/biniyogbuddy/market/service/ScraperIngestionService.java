package com.biniyogbuddy.market.service;

import com.biniyogbuddy.common.dto.ScrapedPayload;
import com.biniyogbuddy.common.dto.ScrapedPayload.IndexDataDto;
import com.biniyogbuddy.common.dto.ScrapedPayload.MarketStatsDto;
import com.biniyogbuddy.common.dto.ScrapedPayload.StockRowDto;
import com.biniyogbuddy.market.entity.IndexHistory;
import com.biniyogbuddy.market.entity.IndexName;
import com.biniyogbuddy.market.entity.MarketSnapshot;
import com.biniyogbuddy.market.entity.MarketStatus;
import com.biniyogbuddy.market.repository.IndexHistoryRepository;
import com.biniyogbuddy.market.repository.MarketSnapshotRepository;
import com.biniyogbuddy.stocks.entity.Stock;
import com.biniyogbuddy.stocks.entity.StockPrice;
import com.biniyogbuddy.stocks.entity.StockStatus;
import com.biniyogbuddy.stocks.repository.StockPriceRepository;
import com.biniyogbuddy.stocks.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScraperIngestionService {

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final MarketSnapshotRepository marketSnapshotRepository;
    private final IndexHistoryRepository indexHistoryRepository;

    @Transactional
    public void ingest(ScrapedPayload payload) {
        LocalDate today = LocalDate.now();
        ingestStockPrices(payload.getStockRows());
        ingestMarketSnapshot(payload.getIndexData(), payload.getMarketStats(), payload.getMarketStatus(), today);
        ingestIndexHistory(payload.getIndexData(), today);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void ingestStockPrices(List<StockRowDto> rows) {
        Map<String, Stock> stockMap = new java.util.HashMap<>(
                stockRepository.findAll().stream()
                        .collect(Collectors.toMap(Stock::getTradingCode, s -> s))
        );

        List<Stock> newStocks = rows.stream()
                .filter(row -> !stockMap.containsKey(row.getTradingCode()))
                .map(row -> Stock.builder()
                        .tradingCode(row.getTradingCode())
                        .companyName(row.getTradingCode())
                        .status(StockStatus.LISTED)
                        .build())
                .toList();

        if (!newStocks.isEmpty()) {
            stockRepository.saveAll(newStocks)
                    .forEach(s -> stockMap.put(s.getTradingCode(), s));
            log.info("Auto-created {} new stock entries", newStocks.size());
        }

        Map<Long, StockPrice> priceMap = stockPriceRepository.findAll().stream()
                .collect(Collectors.toMap(sp -> sp.getStock().getId(), sp -> sp));

        List<StockPrice> toSave = new ArrayList<>();

        for (StockRowDto row : rows) {
            Stock stock = stockMap.get(row.getTradingCode());
            if (stock == null) continue;

            StockPrice price = priceMap.getOrDefault(
                    stock.getId(),
                    StockPrice.builder().stock(stock).build()
            );

            price.setLtp(parseBigDecimal(row.getLtp()));
            price.setHigh(parseBigDecimal(row.getHigh()));
            price.setLow(parseBigDecimal(row.getLow()));
            price.setClosePrice(parseBigDecimal(row.getClosePrice()));
            price.setYesterdayClose(parseBigDecimal(row.getYesterdayClose()));
            price.setChange(parseBigDecimal(row.getChange()));
            price.setVolume(parseLong(row.getVolume()));
            price.setValueMn(parseBigDecimal(row.getValueMn()));
            price.setTrades(parseInt(row.getTrades()));
            price.setFetchedAt(LocalDateTime.now());

            toSave.add(price);
        }

        stockPriceRepository.saveAll(toSave);
        log.info("Stock prices ingested: count={}", toSave.size());
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void ingestMarketSnapshot(List<IndexDataDto> indexData,
                                      MarketStatsDto stats,
                                      String marketStatus,
                                      LocalDate tradeDate) {
        Map<String, IndexDataDto> idxMap = indexData.stream()
                .collect(Collectors.toMap(IndexDataDto::getName, i -> i, (a, b) -> a));

        IndexDataDto dsex = idxMap.get("DSEX");
        IndexDataDto dses = idxMap.get("DSES");
        IndexDataDto ds30 = idxMap.get("DS30");

        MarketSnapshot snapshot = MarketSnapshot.builder()
                .dsexValue(dsex != null ? parseBigDecimal(dsex.getValue()) : null)
                .dsexChange(dsex != null ? parseBigDecimal(dsex.getChange()) : null)
                .dsexChangePct(dsex != null ? parsePct(dsex.getChangePct()) : null)
                .dsesValue(dses != null ? parseBigDecimal(dses.getValue()) : null)
                .dsesChange(dses != null ? parseBigDecimal(dses.getChange()) : null)
                .dsesChangePct(dses != null ? parsePct(dses.getChangePct()) : null)
                .ds30Value(ds30 != null ? parseBigDecimal(ds30.getValue()) : null)
                .ds30Change(ds30 != null ? parseBigDecimal(ds30.getChange()) : null)
                .ds30ChangePct(ds30 != null ? parsePct(ds30.getChangePct()) : null)
                .marketStatus(toMarketStatus(marketStatus))
                .totalTurnoverMn(parseBigDecimal(stats.getTotalValueMn()))
                .totalTrades(parseInt(stats.getTotalTrade()))
                .totalVolume(parseLong(stats.getTotalVolume()))
                .advances(parseInt(stats.getAdvances()))
                .declines(parseInt(stats.getDeclines()))
                .unchanged(parseInt(stats.getUnchanged()))
                .tradeDate(tradeDate)
                .fetchedAt(LocalDateTime.now())
                .build();

        marketSnapshotRepository.save(snapshot);
        log.info("Market snapshot saved: date={} status={}", tradeDate, marketStatus);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void ingestIndexHistory(List<IndexDataDto> indexData, LocalDate tradeDate) {
        for (IndexDataDto raw : indexData) {
            IndexName indexName = IndexName.valueOf(raw.getName());

            IndexHistory history = indexHistoryRepository
                    .findByIndexNameAndTradeDate(indexName, tradeDate)
                    .orElseGet(() -> IndexHistory.builder()
                            .indexName(indexName)
                            .tradeDate(tradeDate)
                            .build());

            history.setClose(parseBigDecimal(raw.getValue()));
            history.setChange(parseBigDecimal(raw.getChange()));
            history.setChangePct(parsePct(raw.getChangePct()));

            indexHistoryRepository.save(history);
        }
        log.info("Index history upserted: count={} date={}", indexData.size(), tradeDate);
    }

    // ─── Parsers ──────────────────────────────────────────────────────────────

    private BigDecimal parseBigDecimal(String s) {
        if (s == null || s.isBlank() || s.equals("-")) return null;
        try {
            return new BigDecimal(s.replace(",", "").trim());
        } catch (NumberFormatException e) {
            log.debug("Cannot parse BigDecimal: '{}'", s);
            return null;
        }
    }

    private BigDecimal parsePct(String s) {
        if (s == null || s.isBlank() || s.equals("-")) return null;
        return parseBigDecimal(s.replace("%", ""));
    }

    private Integer parseInt(String s) {
        if (s == null || s.isBlank() || s.equals("-")) return null;
        try {
            return Integer.parseInt(s.replace(",", "").trim());
        } catch (NumberFormatException e) {
            log.debug("Cannot parse Integer: '{}'", s);
            return null;
        }
    }

    private Long parseLong(String s) {
        if (s == null || s.isBlank() || s.equals("-")) return null;
        try {
            return Long.parseLong(s.replace(",", "").trim());
        } catch (NumberFormatException e) {
            log.debug("Cannot parse Long: '{}'", s);
            return null;
        }
    }

    private MarketStatus toMarketStatus(String s) {
        return switch (s) {
            case "open"     -> MarketStatus.OPEN;
            case "pre_open" -> MarketStatus.PRE_OPEN;
            default         -> MarketStatus.CLOSED;
        };
    }
}
