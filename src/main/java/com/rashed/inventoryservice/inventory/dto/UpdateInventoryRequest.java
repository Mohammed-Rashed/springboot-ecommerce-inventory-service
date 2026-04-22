package com.rashed.inventoryservice.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateInventoryRequest(

        @NotBlank(message = "sku is required")
        String sku,

        @NotNull(message = "availableQuantity is required")
        @Min(value = 0, message = "availableQuantity must be greater than or equal to 0")
        Integer availableQuantity
) {
}