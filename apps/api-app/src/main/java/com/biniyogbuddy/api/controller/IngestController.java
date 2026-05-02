package com.biniyogbuddy.api.controller;

import com.biniyogbuddy.common.dto.ScrapedPayload;
import com.biniyogbuddy.market.service.ScraperIngestionService;
import com.biniyogbuddy.stocks.dto.SectorStockMappingRequest;
import com.biniyogbuddy.stocks.service.SectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class IngestController {

    private final ScraperIngestionService ingestionService;
    private final SectorService sectorService;

    @Value("${internal.api-key}")
    private String apiKey;

    @PostMapping("/ingest")
    public ResponseEntity<Void> ingest(
            @RequestHeader("X-Internal-Key") String key,
            @RequestBody ScrapedPayload payload) {

        if (!apiKey.equals(key)) {
            log.warn("POST /internal/ingest — unauthorized request (invalid API key)");
            return ResponseEntity.status(401).build();
        }
        log.info("POST /internal/ingest — received: stocks={} indices={} status={}",
                payload.getStockRows() != null ? payload.getStockRows().size() : 0,
                payload.getIndexData() != null ? payload.getIndexData().size() : 0,
                payload.getMarketStatus());
        ingestionService.ingest(payload);
        log.info("POST /internal/ingest — completed successfully");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ingest/sector-stocks")
    public ResponseEntity<Void> ingestSectorStocks(
            @RequestHeader("X-Internal-Key") String key,
            @RequestBody List<SectorStockMappingRequest> mappings) {

        if (!apiKey.equals(key)) {
            log.warn("POST /internal/ingest/sector-stocks — unauthorized request (invalid API key)");
            return ResponseEntity.status(401).build();
        }
        log.info("POST /internal/ingest/sector-stocks — received: sectors={}", mappings.size());
        sectorService.ingestSectorMappings(mappings);
        log.info("POST /internal/ingest/sector-stocks — completed successfully");
        return ResponseEntity.ok().build();
    }
}
