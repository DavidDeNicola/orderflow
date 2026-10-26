package com.orderflow.orderservice.dto;

import com.orderflow.orderservice.model.OrderStatus;
import com.orderflow.orderservice.model.details.OrderDetails;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(Long id, String customerId, OrderStatus status, BigDecimal totalAmount, Instant createdAt, OrderDetails details) {
}
