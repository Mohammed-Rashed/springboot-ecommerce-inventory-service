package com.rashed.inventoryservice.inventory.dto;

import java.time.LocalDateTime;

public record InventoryResponse(
        Long id,
        Long productId,
        String sku,
        Integer availableQuantity,
        Integer reservedQuantity,
        LocalDateTime updatedAt
) {
}