package com.example.JWTlogin.Config;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreakerConfig defaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Define the failure rate threshold
                .waitDurationInOpenState(Duration.ofMillis(10000)) // Time the circuit stays open before retrying
                .slidingWindowSize(100) // Size of the sliding window for failure rate calculation
                .build();
    }
}
