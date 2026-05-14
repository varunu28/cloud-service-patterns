package com.saga.orderservice.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class TopicConfig {

    @Bean
    fun orderCreatedTopic(): NewTopic {
        return TopicBuilder.name("order-created").build()
    }
}