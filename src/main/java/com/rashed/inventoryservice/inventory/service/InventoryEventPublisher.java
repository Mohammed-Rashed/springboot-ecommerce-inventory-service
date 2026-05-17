package com.rashed.inventoryservice.inventory.service;


import com.rashed.inventoryservice.common.config.KafkaTopicsProperties;
import com.rashed.inventoryservice.inventory.events.StockRejectedEvent;
import com.rashed.inventoryservice.inventory.events.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;
    public void publishStockReserved(StockReservedEvent event) {
        String topic = kafkaTopicsProperties.stockReserved();
        String key = String.valueOf(event.orderId());

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error(
                                "Failed to publish stock-reserved event. orderId={}, topic={}",
                                event.orderId(),
                                topic,
                                exception
                        );
                        return;
                    }

                    log.info(
                            "Published stock-reserved event. orderId={}, topic={}, partition={}, offset={}",
                            event.orderId(),
                            topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                });
    }

    public void publishStockRejected(StockRejectedEvent event) {
        String topic = kafkaTopicsProperties.stockRejected();
        String key = String.valueOf(event.orderId());

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error(
                                "Failed to publish stock-rejected event. orderId={}, topic={}, reason={}",
                                event.orderId(),
                                topic,
                                event.reason(),
                                exception
                        );
                        return;
                    }

                    log.info(
                            "Published stock-rejected event. orderId={}, topic={}, partition={}, offset={}, reason={}",
                            event.orderId(),
                            topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event.reason()
                    );
                });
    }
}
