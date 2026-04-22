package com.rashed.inventoryservice.inventory.controller;


import com.rashed.inventoryservice.inventory.dto.*;
import com.rashed.inventoryservice.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse create(@Valid @RequestBody CreateInventoryRequest request) {
        return inventoryService.create(request);
    }
    @PutMapping("/{productId}")
    public InventoryResponse update(@PathVariable Long productId,
                                    @Valid @RequestBody UpdateInventoryRequest request) {
        return inventoryService.update(productId, request);
    }

    @GetMapping("/{productId}")
    public InventoryResponse getByProductId(@PathVariable Long productId) {
        return inventoryService.getByProductId(productId);
    }

    @PostMapping("/check")
    public CheckAvailabilityResponse checkAvailability(@Valid @RequestBody CheckAvailabilityRequest request) {
        return inventoryService.checkAvailability(request);
    }
    @PostMapping("/reserve")
    public StockOperationResponse reserve(@Valid @RequestBody ReserveStockRequest request) {
        return inventoryService.reserve(request);
    }

    @PostMapping("/release")
    public StockOperationResponse release(@Valid @RequestBody ReleaseStockRequest request) {
        return inventoryService.release(request);
    }

    @PostMapping("/deduct")
    public StockOperationResponse deduct(@Valid @RequestBody DeductStockRequest request) {
        return inventoryService.deduct(request);
    }
}
