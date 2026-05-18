package com.rashed.inventoryservice.inventory.events;

public record StockReservedEvent(
        Long orderId
) {
}
