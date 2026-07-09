package com.fooddelivery.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic orderPlacedTopic()
    {
        return TopicBuilder.name("order.placed")
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name("order.confirmed")  // no space
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic orderDeliveredTopic()
    {
        return TopicBuilder.name("order.delivered")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
