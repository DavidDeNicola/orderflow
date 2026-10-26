package com.orderflow.orderservice.mapper;

import com.orderflow.orderservice.dto.OrderResponse;
import com.orderflow.orderservice.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getId(), order.getCustomerId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt(), order.getDetails());
    }
}
