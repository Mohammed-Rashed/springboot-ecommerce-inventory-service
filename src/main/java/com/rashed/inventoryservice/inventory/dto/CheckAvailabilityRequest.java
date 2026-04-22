package com.rashed.inventoryservice.inventory.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CheckAvailabilityRequest(

        @NotNull(message = "productId is required")
        Long productId,

        @NotNull(message = "requestedQuantity is required")
        @Min(value = 1, message = "requestedQuantity must be greater than or equal to 1")
        Integer requestedQuantity
) {
}