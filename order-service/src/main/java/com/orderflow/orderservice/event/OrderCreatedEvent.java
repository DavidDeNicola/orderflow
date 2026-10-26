package com.orderflow.orderservice.event;

import java.time.Instant;
import java.util.List;

public record OrderCreatedEvent(String eventId, Long orderId, String customerId, List<ItemEvent> items, Instant createdAt) {

    public record ItemEvent(String productCode, int quantity) {
    }
}


