package com.biniyogbuddy.api.controller.trade;

import com.biniyogbuddy.common.config.MessageResource;
import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.common.dto.MessageResponse;
import com.biniyogbuddy.trades.dto.TradeJournalRequest;
import com.biniyogbuddy.trades.dto.TradeJournalResponse;
import com.biniyogbuddy.trades.entity.TradeDirection;
import com.biniyogbuddy.trades.service.TradeJournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trade-journal")
@RequiredArgsConstructor
public class TradeJournalController {

    private final TradeJournalService tradeJournalService;
    private final MessageResource messageResource;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TradeJournalResponse>>> getAll(
            @RequestParam(required = false) Long stockId,
            @RequestParam(required = false) TradeDirection tradeDirection,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tradeDate,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        List<TradeJournalResponse> trades = tradeJournalService.getAll(stockId, tradeDirection, tradeDate, sortBy, sortDir);
        String message = messageResource.getMessage("trade.journal.list.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", trades));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TradeJournalResponse>> create(@Valid @RequestBody TradeJournalRequest request) {
        TradeJournalResponse trade = tradeJournalService.create(request);
        String message = messageResource.getMessage("trade.journal.create.success");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(message, "success", trade));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeJournalResponse>> getById(@PathVariable Long id) {
        TradeJournalResponse trade = tradeJournalService.getById(id);
        String message = messageResource.getMessage("trade.journal.get.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", trade));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TradeJournalResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TradeJournalRequest request) {
        TradeJournalResponse trade = tradeJournalService.update(id, request);
        String message = messageResource.getMessage("trade.journal.update.success");
        return ResponseEntity.ok(new ApiResponse<>(message, "success", trade));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        tradeJournalService.delete(id);
        String message = messageResource.getMessage("trade.journal.delete.success");
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
