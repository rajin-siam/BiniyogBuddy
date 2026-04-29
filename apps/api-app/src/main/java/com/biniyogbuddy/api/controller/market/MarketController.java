package com.biniyogbuddy.api.controller.market;

import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.market.dto.MarketStatusResponse;
import com.biniyogbuddy.market.dto.MarketSummaryResponse;
import com.biniyogbuddy.market.service.MarketSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketSnapshotService marketSnapshotService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MarketSummaryResponse>> getSummary() {
        MarketSummaryResponse summary = marketSnapshotService.getLatestSummary();
        return ResponseEntity.ok(new ApiResponse<>("Market summary fetched successfully", "success", summary));
    }

    @GetMapping("/status")
    public ResponseEntity<MarketStatusResponse> getMarketStatus() {
        return ResponseEntity.ok(marketSnapshotService.findLatestMarketStatus());
    }
}
