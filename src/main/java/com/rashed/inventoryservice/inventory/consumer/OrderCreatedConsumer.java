package com.rashed.inventoryservice.inventory.consumer;


import com.rashed.inventoryservice.inventory.events.OrderCreatedEvent;
import com.rashed.inventoryservice.inventory.events.StockReservedEvent;
import com.rashed.inventoryservice.inventory.service.InventoryEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final InventoryEventPublisher inventoryEventPublisher;

    @KafkaListener(topics = "${app.kafka.topics.order-created}")
    public void consumeOrderCreated(@Payload(required = false) OrderCreatedEvent event) {
        if (event == null) {
            log.warn("Received empty order-created event payload");
            return;
        }

        log.info(
                "Received order-created event. orderId={}, customerId={}, itemsCount={}",
                event.orderId(),
                event.customerId(),
                event.items().size()
        );
        event.items().forEach(item ->
                log.info(
                        "Order item received. orderId={}, productId={}, quantity={}",
                        event.orderId(),
                        item.productId(),
                        item.quantity()
                )
        );
        StockReservedEvent stockReservedEvent = new StockReservedEvent(event.orderId());

        inventoryEventPublisher.publishStockReserved(stockReservedEvent);

    }
}
