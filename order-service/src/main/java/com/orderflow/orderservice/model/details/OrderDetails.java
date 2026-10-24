package com.orderflow.orderservice.model.details;

import java.util.List;

public record OrderDetails(String shipping, List<OrderItem> items, Boolean giftWrap, String notes) {
}
