package com.lumos.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.lumos")
@EntityScan(basePackages = "com.lumos.infra.persistence.entity")
@EnableJpaRepositories(basePackages = "com.lumos.infra.persistence.repository")
@org.springframework.data.jpa.repository.config.EnableJpaAuditing
public class LumosApplication {
    public static void main(String[] args) {
        SpringApplication.run(LumosApplication.class, args);
    }
}
