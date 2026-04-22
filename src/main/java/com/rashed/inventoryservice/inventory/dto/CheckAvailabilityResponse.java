package com.rashed.inventoryservice.inventory.dto;


public record CheckAvailabilityResponse(
        Long productId,
        Integer requestedQuantity,
        Integer availableQuantity,
        boolean available
) {
}