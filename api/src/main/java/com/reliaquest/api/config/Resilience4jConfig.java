package com.reliaquest.api.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * resilience4j beans configurations
 */
@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreaker circuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.ofDefaults();
        return CircuitBreaker.of("employeeService", circuitBreakerConfig);
    }

    @Bean
    public Retry retry() {
        RetryConfig retryConfig = RetryConfig.ofDefaults();
        return Retry.of("employeeService", retryConfig);
    }

    @Bean
    public Bulkhead bulkhead() {
        BulkheadConfig bulkheadConfig = BulkheadConfig.ofDefaults();
        return Bulkhead.of("employeeService", bulkheadConfig);
    }
}
