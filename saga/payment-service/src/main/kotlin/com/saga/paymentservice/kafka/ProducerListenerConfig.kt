package com.saga.paymentservice.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.ProducerListener

@Configuration
class ProducerListenerConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun producerListener(): ProducerListener<Any, Any> {
        return object : ProducerListener<Any, Any> {
            override fun onSuccess(producerRecord: ProducerRecord<Any, Any>, recordMetadata: RecordMetadata) {
                logger.info(
                    "Sent message to topic: {} with record: {}",
                    producerRecord.topic(), producerRecord.value()
                )
            }

            override fun onError(
                producerRecord: ProducerRecord<Any, Any>,
                recordMetadata: RecordMetadata?,
                exception: Exception
            ) {
                logger.info(
                    "Failed to send message to topic: {} due to: {}",
                    producerRecord.topic(), exception.message
                )
            }
        }
    }
}