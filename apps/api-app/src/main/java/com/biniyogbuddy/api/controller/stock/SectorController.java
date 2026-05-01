package com.biniyogbuddy.api.controller.stock;

import com.biniyogbuddy.common.dto.ApiResponse;
import com.biniyogbuddy.stocks.dto.SectorResponse;
import com.biniyogbuddy.stocks.service.SectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sectors")
@RequiredArgsConstructor
public class SectorController {

    private final SectorService sectorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SectorResponse>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>("success", "success", sectorService.getAll()));
    }
}
