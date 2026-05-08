package com.rashed.inventoryservice.inventory.events;

public record StockRejectedEvent(
        Long orderId,
        String reason
) {
}
