package com.saga.orderservice.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.ProducerListener


@Configuration
class ProducerConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    fun producerConfig(): Map<String, Any> {
        return mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
        )
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(producerConfig())
    }

    @Bean
    fun producerListener(): ProducerListener<String, String> {
        return object : ProducerListener<String, String> {
            override fun onSuccess(producerRecord: ProducerRecord<String, String>, recordMetadata: RecordMetadata) {
                logger.info(
                    "Sent message to topic: {} with record: {}",
                    producerRecord.topic(), producerRecord.value()
                )
            }

            override fun onError(
                producerRecord: ProducerRecord<String, String>,
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

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
        val template = KafkaTemplate(producerFactory)
        template.setProducerListener(producerListener())
        return template
    }
}