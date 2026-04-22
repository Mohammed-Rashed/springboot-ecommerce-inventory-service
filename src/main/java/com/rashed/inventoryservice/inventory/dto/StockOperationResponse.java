package com.rashed.inventoryservice.inventory.dto;

import java.time.LocalDateTime;

public record StockOperationResponse(
        Long productId,
        String sku,
        Integer availableQuantity,
        Integer reservedQuantity,
        String message,
        LocalDateTime updatedAt
) {
}
