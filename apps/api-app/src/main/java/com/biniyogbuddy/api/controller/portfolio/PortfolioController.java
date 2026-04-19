package com.biniyogbuddy.api.controller.portfolio;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.trades.dto.HoldingResponse;
import com.biniyogbuddy.trades.dto.PortfolioSummaryResponse;
import com.biniyogbuddy.trades.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final MessageResource messageResource;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>> getSummary() {
        PortfolioSummaryResponse summary = portfolioService.getSummary();
        String message = messageResource.getMessage("portfolio.summary.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", summary));
    }

    @GetMapping("/holdings")
    public ResponseEntity<ApiResponse<List<HoldingResponse>>> getHoldings() {
        List<HoldingResponse> holdings = portfolioService.getHoldings();
        String message = messageResource.getMessage("portfolio.holdings.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", holdings));
    }
}
