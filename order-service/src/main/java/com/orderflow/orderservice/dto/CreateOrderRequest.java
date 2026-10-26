package com.orderflow.orderservice.dto;

import com.orderflow.orderservice.model.details.OrderItem;
import com.orderflow.orderservice.model.details.ShippingAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(@NotBlank String customerId, @NotNull @Valid ShippingAddress shipping, @NotEmpty List<@Valid OrderItem> items, Boolean giftWrap, String notes) {
}
