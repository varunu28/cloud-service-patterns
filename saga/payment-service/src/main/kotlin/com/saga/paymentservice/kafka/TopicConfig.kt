package com.saga.paymentservice.kafka

import com.saga.paymentservice.kafka.KafkaTopics.PAYMENT_FAILED_TOPIC
import com.saga.paymentservice.kafka.KafkaTopics.PAYMENT_SUCCEEDED_TOPIC
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class TopicConfig {

    @Bean
    fun paymentSucceededTopic(): NewTopic {
        return TopicBuilder.name(PAYMENT_SUCCEEDED_TOPIC).build()
    }

    @Bean
    fun paymentFailedTopic(): NewTopic {
        return TopicBuilder.name(PAYMENT_FAILED_TOPIC).build()
    }
}