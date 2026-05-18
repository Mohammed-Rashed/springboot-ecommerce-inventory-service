package com.rashed.inventoryservice.inventory.events;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        List<OrderCreatedItemEvent> items
) {
}
