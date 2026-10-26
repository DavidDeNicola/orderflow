package com.orderflow.orderservice.model.details;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItem(@NotBlank String productCode, @NotNull @Positive Integer quantity, @NotNull @Positive BigDecimal unitPrice) {
}
