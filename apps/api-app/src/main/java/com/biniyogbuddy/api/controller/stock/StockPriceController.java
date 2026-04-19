package com.biniyogbuddy.api.controller.stock;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.common.dto.MessageResponse;
import com.biniyogbuddy.stocks.dto.StockPriceRequest;
import com.biniyogbuddy.stocks.dto.StockPriceResponse;
import com.biniyogbuddy.stocks.service.StockPriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks/{stockId}/prices")
@RequiredArgsConstructor
public class StockPriceController {

    private final StockPriceService stockPriceService;
    private final MessageResource messageResource;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockPriceResponse>>> getHistory(
            @PathVariable Long stockId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<StockPriceResponse> prices = stockPriceService.getHistory(stockId, from, to);
        String message = messageResource.getMessage("stock.price.list.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", prices));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StockPriceResponse>> create(
            @PathVariable Long stockId,
            @Valid @RequestBody StockPriceRequest request) {
        StockPriceResponse price = stockPriceService.create(stockId, request);
        String message = messageResource.getMessage("stock.price.create.success");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(message, "success", price));
    }

    @DeleteMapping("/{priceId}")
    public ResponseEntity<MessageResponse> delete(
            @PathVariable Long stockId,
            @PathVariable Long priceId) {
        stockPriceService.delete(stockId, priceId);
        String message = messageResource.getMessage("stock.price.delete.success");
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
