package com.saga.orderservice.saga

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {

    @Bean
    @Qualifier("paymentServiceRestClient")
    fun paymentServiceRestClient(@Value($$"${payment.service.url}") baseUrl: String): RestClient {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .build()
    }

    @Bean
    @Qualifier("inventoryServiceRestClient")
    fun inventoryServiceRestClient(@Value($$"${inventory.service.url}") baseUrl: String): RestClient {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .build()
    }
}