package com.orderflow.inventoryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
public class KafkaConfig {

    public static final String ORDERS_CREATED_TOPIC = "orders-created";
    public static final String ORDERS_CREATED_DLT = "orders-created-dlt";

    @Bean
    public NewTopic ordersCreatedDltTopic() {
        return new NewTopic(ORDERS_CREATED_DLT, 3, (short) 1);
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template));
    }
}
