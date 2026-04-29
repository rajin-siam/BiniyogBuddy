package com.biniyogbuddy.api.controller.stock;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.common.dto.MessageResponse;
import com.biniyogbuddy.stocks.dto.StockRequest;
import com.biniyogbuddy.stocks.dto.StockResponse;
import com.biniyogbuddy.stocks.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final MessageResource messageResource;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockResponse>>> getAll() {
        List<StockResponse> stocks = stockService.getAll();
        String message = messageResource.getMessage("stock.list.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", stocks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> getById(@PathVariable Long id) {
        StockResponse stock = stockService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "success", stock));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> create(@Valid @RequestBody StockRequest request) {
        StockResponse stock = stockService.create(request);
        String message = messageResource.getMessage("stock.create.success");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(message, "success", stock));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StockRequest request) {
        StockResponse stock = stockService.update(id, request);
        String message = messageResource.getMessage("stock.update.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", stock));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        stockService.delete(id);
        String message = messageResource.getMessage("stock.delete.success");
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
