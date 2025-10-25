package com.varunu28.orderservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange("payment.events");
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("order.events");
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable("order-created-queue").build();
    }

    @Bean
    public Queue paymentCreatedQueue() {
        return QueueBuilder.durable("payment-created-queue").build();
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
            .bind(orderCreatedQueue())
            .to(orderExchange())
            .with("order.created");
    }

    @Bean
    public Binding paymentCreatedBinding() {
        return BindingBuilder
            .bind(paymentCreatedQueue())
            .to(paymentExchange())
            .with("payment.created");
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        // This is required for RabbitMQ to trust the packages when deserializing messages
        classMapper.setTrustedPackages("java.util", "com.varunu28.orderservice");
        return classMapper;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setClassMapper(classMapper());
        return jackson2JsonMessageConverter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }
}
