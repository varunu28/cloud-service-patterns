package com.varunu28.nameservice;

import com.varunu28.nameservice.properties.NameProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(NameProperties.class)
public class NameServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NameServiceApplication.class, args);
    }
}
