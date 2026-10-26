package com.orderflow.orderservice.controller;

import com.orderflow.orderservice.dto.CreateOrderRequest;
import com.orderflow.orderservice.dto.OrderResponse;
import com.orderflow.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(createOrderRequest));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(@RequestParam String customerId) {
        return ResponseEntity.ok(orderService.findByCustomer(customerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrderResponse>> getOrdersByDetails(@RequestParam(required = false) String city, @RequestParam(required = false) String productCode) {
        if(city != null && !city.isBlank() && productCode == null){
            return ResponseEntity.ok(orderService.findByCity(city));
        }
        if(productCode != null && !productCode.isBlank() && city == null){
            return ResponseEntity.ok(orderService.findByProductCode(productCode));
        }
        return ResponseEntity.badRequest().build();
    }

}
