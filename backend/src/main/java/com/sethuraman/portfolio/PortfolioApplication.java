package com.sethuraman.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the J. Sethuraman Portfolio Backend.
 * Spring Boot 3.3.5 application with REST APIs, Spring Security, and JPA.
 */
@SpringBootApplication
public class PortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }
}
