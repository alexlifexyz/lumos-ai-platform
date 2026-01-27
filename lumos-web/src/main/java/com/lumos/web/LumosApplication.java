package com.lumos.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lumos")
public class LumosApplication {
    public static void main(String[] args) {
        SpringApplication.run(LumosApplication.class, args);
    }
}
