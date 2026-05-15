package com.saga.inventoryservice.kafka

import com.saga.inventoryservice.kafka.KafkaTopics.INVENTORY_RESERVATION_FAILED_TOPIC
import com.saga.inventoryservice.kafka.KafkaTopics.INVENTORY_RESERVATION_SUCCESSFUL_TOPIC
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class TopicConfig {

    @Bean
    fun inventoryReservationSucceededTopic(): NewTopic {
        return TopicBuilder.name(INVENTORY_RESERVATION_SUCCESSFUL_TOPIC).build()
    }

    @Bean
    fun inventoryReservationFailedTopic(): NewTopic {
        return TopicBuilder.name(INVENTORY_RESERVATION_FAILED_TOPIC).build()
    }
}