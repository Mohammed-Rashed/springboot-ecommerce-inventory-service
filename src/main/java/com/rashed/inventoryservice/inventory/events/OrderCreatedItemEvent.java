package com.rashed.inventoryservice.inventory.events;

public record OrderCreatedItemEvent(
        Long productId,
        Integer quantity
) {
}
