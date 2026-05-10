package com.rashed.inventoryservice.inventory.consumer;


import com.rashed.inventoryservice.inventory.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCreatedConsumer {
    @KafkaListener(topics = "${app.kafka.topics.order-created}")
    public void consumeOrderCreated(OrderCreatedEvent event) {
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
    }
}
