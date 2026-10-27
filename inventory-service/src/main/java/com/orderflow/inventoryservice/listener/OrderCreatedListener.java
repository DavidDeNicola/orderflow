package com.orderflow.inventoryservice.listener;

import com.orderflow.inventoryservice.config.KafkaConfig;
import com.orderflow.inventoryservice.event.OrderCreatedEvent;
import com.orderflow.inventoryservice.service.InventoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);
    private final InventoryService inventoryService;

    public OrderCreatedListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = KafkaConfig.ORDERS_CREATED_TOPIC, groupId = "inventory-service")
    public void listen(ConsumerRecord<String, OrderCreatedEvent> record) {
        log.info("Ricevuto evento: key={}, partition={}, offset={}, value={}", record.key(), record.partition(), record.offset(), record.value());
        inventoryService.handleOrderCreated(record.value());
    }
}
