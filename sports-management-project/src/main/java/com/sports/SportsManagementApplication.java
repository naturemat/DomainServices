package com.sports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sports", "application", "domain", "infrastructure", "shared"})
public class SportsManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportsManagementApplication.class, args);
    }
}