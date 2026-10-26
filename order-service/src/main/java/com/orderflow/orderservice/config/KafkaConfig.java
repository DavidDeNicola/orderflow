package com.orderflow.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    public static final String ORDERS_CREATED_TOPIC = "orders-created";

    @Bean
    public NewTopic ordersCreatedTopic() {
        return new NewTopic(ORDERS_CREATED_TOPIC, 3, (short) 1);
    }
}
