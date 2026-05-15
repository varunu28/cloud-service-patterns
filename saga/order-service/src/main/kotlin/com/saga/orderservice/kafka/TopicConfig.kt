package com.saga.orderservice.kafka

import com.saga.orderservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC
import com.saga.orderservice.kafka.KafkaTopics.ORDER_FAILED_TOPIC
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class TopicConfig {

    @Bean
    fun orderCreatedTopic(): NewTopic {
        return TopicBuilder.name(ORDER_CREATED_TOPIC).build()
    }

    @Bean
    fun orderFailedTopic(): NewTopic {
        return TopicBuilder.name(ORDER_FAILED_TOPIC).build()
    }
}