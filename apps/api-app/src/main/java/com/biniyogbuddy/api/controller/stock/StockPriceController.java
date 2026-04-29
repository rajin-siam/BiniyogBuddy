package com.biniyogbuddy.api.controller.stock;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.stocks.dto.StockPriceResponse;
import com.biniyogbuddy.stocks.service.StockPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stocks/{stockId}/prices")
@RequiredArgsConstructor
public class StockPriceController {

    private final StockPriceService stockPriceService;
    private final MessageResource messageResource;

    @GetMapping
    public ResponseEntity<ApiResponse<StockPriceResponse>> getCurrentPrice(@PathVariable Long stockId) {
        StockPriceResponse price = stockPriceService.getCurrentPrice(stockId);
        String message = messageResource.getMessage("stock.price.list.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", price));
    }
}
