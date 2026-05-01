package com.biniyogbuddy.api.controller;

import com.biniyogbuddy.common.dto.ScrapedPayload;
import com.biniyogbuddy.market.service.ScraperIngestionService;
import com.biniyogbuddy.stocks.dto.SectorStockMappingRequest;
import com.biniyogbuddy.stocks.service.SectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        if (!apiKey.equals(key)) return ResponseEntity.status(401).build();
        ingestionService.ingest(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ingest/sector-stocks")
    public ResponseEntity<Void> ingestSectorStocks(
            @RequestHeader("X-Internal-Key") String key,
            @RequestBody List<SectorStockMappingRequest> mappings) {

        if (!apiKey.equals(key)) return ResponseEntity.status(401).build();
        sectorService.ingestSectorMappings(mappings);
        return ResponseEntity.ok().build();
    }
}
