package com.orderflow.orderservice.model.details;

import java.math.BigDecimal;

public record OrderItem(String productCode, Integer quantity, BigDecimal unitPrice) {
}
