package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableAsync
@EnableWebSecurity
public class InventoryTrackingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryTrackingPlatformApplication.class, args);
    }
}