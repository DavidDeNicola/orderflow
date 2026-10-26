package com.orderflow.orderservice.service;

import com.orderflow.orderservice.config.KafkaConfig;
import com.orderflow.orderservice.dto.CreateOrderRequest;
import com.orderflow.orderservice.dto.OrderResponse;
import com.orderflow.orderservice.event.OrderCreatedEvent;
import com.orderflow.orderservice.mapper.OrderMapper;
import com.orderflow.orderservice.model.Order;
import com.orderflow.orderservice.model.OrderStatus;
import com.orderflow.orderservice.model.details.OrderDetails;
import com.orderflow.orderservice.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.kafkaTemplate = kafkaTemplate;
    }


    public OrderResponse createOrder(CreateOrderRequest request) {
        BigDecimal totalAmount = request.items().stream()
                .map(item -> item.unitPrice().multiply(new BigDecimal(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderStatus status = OrderStatus.CREATED;

        OrderDetails details = new OrderDetails(request.shipping(), request.items(), request.giftWrap(), request.notes());

        Order saved = orderRepository.save(new Order(request.customerId(), status, totalAmount, details));

        OrderCreatedEvent event = orderMapper.toCreatedEvent(saved);
        kafkaTemplate.send(KafkaConfig.ORDERS_CREATED_TOPIC, event.customerId(), event);

        return orderMapper.toResponse(saved);
    }

    public List<OrderResponse> findByCustomer(String customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> findByCity(String city) {
        return orderRepository.findByCity(city).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> findByProductCode(String productCode) {
        return orderRepository.findByProductCode(productCode).stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}
