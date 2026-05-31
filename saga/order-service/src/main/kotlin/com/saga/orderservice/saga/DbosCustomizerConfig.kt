package com.saga.orderservice.saga

import dev.dbos.transact.spring.DBOSConfigCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource


@Configuration
class DbosCustomizerConfig {

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()

        dataSource.setDriverClassName("org.postgresql.Driver")
        dataSource.url = "jdbc:postgresql://localhost:5432/orderdb"
        dataSource.username = "myuser"
        dataSource.password = "secret"
        return dataSource
    }

    @Bean
    @Order(1)
    fun dbosConfigCustomizer(): DBOSConfigCustomizer {
        return DBOSConfigCustomizer { config ->
            config.withDataSource(dataSource())
                .withAppName("order-service")
                .enableAdminServer()
        }
    }
}