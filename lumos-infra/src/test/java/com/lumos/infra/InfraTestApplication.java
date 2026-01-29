package com.lumos.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
@EnableAsync
public class InfraTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(InfraTestApplication.class, args);
    }
}
