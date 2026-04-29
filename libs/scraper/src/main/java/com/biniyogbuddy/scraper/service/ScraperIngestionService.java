package com.biniyogbuddy.scraper.service;

import com.biniyogbuddy.market.entity.IndexHistory;
import com.biniyogbuddy.market.entity.IndexName;
import com.biniyogbuddy.market.entity.MarketSnapshot;
import com.biniyogbuddy.market.entity.MarketStatus;
import com.biniyogbuddy.market.repository.IndexHistoryRepository;
import com.biniyogbuddy.market.repository.MarketSnapshotRepository;
import com.biniyogbuddy.scraper.dto.RawScrapedData.RawIndexData;
import com.biniyogbuddy.scraper.dto.RawScrapedData.RawMarketStats;
import com.biniyogbuddy.scraper.dto.RawScrapedData.RawStockRow;
import com.biniyogbuddy.scraper.dto.RawScrapedData.ScrapedPage;
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
    public void ingest(ScrapedPage page) {
        LocalDate today = LocalDate.now();
        ingestStockPrices(page.getStockRows());
        ingestMarketSnapshot(page.getIndexData(), page.getMarketStats(), page.getMarketStatus(), today);
        ingestIndexHistory(page.getIndexData(), today);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void ingestStockPrices(List<RawStockRow> rows) {
        // batch-load existing stocks
        Map<String, Stock> stockMap = new java.util.HashMap<>(
                stockRepository.findAll().stream()
                        .collect(Collectors.toMap(Stock::getTradingCode, s -> s))
        );

        // auto-create stocks that are listed on DSE but not yet in our DB
        List<Stock> newStocks = rows.stream()
                .filter(row -> !stockMap.containsKey(row.getTradingCode()))
                .map(row -> Stock.builder()
                        .tradingCode(row.getTradingCode())
                        .companyName(row.getTradingCode())   // placeholder — update later
                        .status(StockStatus.LISTED)
                        .build())
                .toList();

        if (!newStocks.isEmpty()) {
            stockRepository.saveAll(newStocks)
                    .forEach(s -> stockMap.put(s.getTradingCode(), s));
            log.info("Auto-created {} new stock entries", newStocks.size());
        }

        // batch-load existing prices
        Map<Long, StockPrice> priceMap = stockPriceRepository.findAll().stream()
                .collect(Collectors.toMap(sp -> sp.getStock().getId(), sp -> sp));

        List<StockPrice> toSave = new ArrayList<>();

        for (RawStockRow row : rows) {
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

    private void ingestMarketSnapshot(List<RawIndexData> indexData,
                                      RawMarketStats stats,
                                      String marketStatus,
                                      LocalDate tradeDate) {
        // DSE page can return the same index twice (current + previous session) — keep first
        Map<String, RawIndexData> idxMap = indexData.stream()
                .collect(Collectors.toMap(RawIndexData::getName, i -> i, (a, b) -> a));

        RawIndexData dsex = idxMap.get("DSEX");
        RawIndexData dses = idxMap.get("DSES");
        RawIndexData ds30 = idxMap.get("DS30");

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

    private void ingestIndexHistory(List<RawIndexData> indexData, LocalDate tradeDate) {
        for (RawIndexData raw : indexData) {
            IndexName indexName = IndexName.valueOf(raw.getName());

            // upsert: update if already saved today, otherwise insert
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
