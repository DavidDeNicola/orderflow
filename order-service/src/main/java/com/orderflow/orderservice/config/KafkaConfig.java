package com.orderflow.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic ordersCreatedTopic() {
        return new NewTopic("orders-created", 3, (short) 1);
    }
}
