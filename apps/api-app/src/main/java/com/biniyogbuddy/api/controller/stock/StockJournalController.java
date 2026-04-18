package com.biniyogbuddy.api.controller.stock;

import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.common.dto.MessageResponse;
import com.biniyogbuddy.stocks.dto.StockJournalRequest;
import com.biniyogbuddy.stocks.dto.StockJournalResponse;
import com.biniyogbuddy.stocks.service.StockJournalService;
import com.biniyogbuddy.common.config.MessageResource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-journal")
@RequiredArgsConstructor
public class StockJournalController {

    private final StockJournalService stockJournalService;
    private final MessageResource messageResource;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockJournalResponse>>> getAll() {
        List<StockJournalResponse> entries = stockJournalService.getAllForCurrentUser();
        String message = messageResource.getMessage("stock.journal.list.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", entries));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StockJournalResponse>> create(@Valid @RequestBody StockJournalRequest request) {
        StockJournalResponse entry = stockJournalService.create(request);
        String message = messageResource.getMessage("stock.journal.create.success");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(message, "success", entry));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StockJournalResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StockJournalRequest request) {
        StockJournalResponse entry = stockJournalService.update(id, request);
        String message = messageResource.getMessage("stock.journal.update.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", entry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        stockJournalService.delete(id);
        String message = messageResource.getMessage("stock.journal.delete.success");
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
