package com.orderflow.inventoryservice.listener;

import com.orderflow.inventoryservice.event.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    @KafkaListener(topics = "orders-created", groupId = "inventory-service")
    public void listen(ConsumerRecord<String, OrderCreatedEvent> record) {
        log.info("Ricevuto evento: key={}, partition={}, offset={}, value={}", record.key(), record.partition(), record.offset(), record.value());
    }
}
