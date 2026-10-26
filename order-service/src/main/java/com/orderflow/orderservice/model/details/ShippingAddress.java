package com.orderflow.orderservice.model.details;

import jakarta.validation.constraints.NotBlank;

public record ShippingAddress(@NotBlank String street, @NotBlank String city, @NotBlank String zip) {
}
