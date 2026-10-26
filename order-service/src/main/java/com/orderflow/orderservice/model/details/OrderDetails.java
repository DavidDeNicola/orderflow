package com.orderflow.orderservice.model.details;

import java.util.List;

public record OrderDetails(ShippingAddress shipping, List<OrderItem> items, Boolean giftWrap, String notes) {
}
