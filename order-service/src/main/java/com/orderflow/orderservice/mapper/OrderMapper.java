package com.orderflow.orderservice.mapper;

import com.orderflow.orderservice.dto.OrderResponse;
import com.orderflow.orderservice.event.OrderCreatedEvent;
import com.orderflow.orderservice.model.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getId(), order.getCustomerId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt(), order.getDetails());
    }

    public OrderCreatedEvent toCreatedEvent(Order order){
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                order.getId(),
                order.getCustomerId(),
                order.getDetails().items().stream().map(item -> new OrderCreatedEvent.ItemEvent(item.productCode(), item.quantity())).toList(),
                order.getCreatedAt()
        );
        return event;
    }
}
