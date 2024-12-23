package com.reliaquest.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final AppConfig appConfig;

    @Bean
    @Qualifier("employeeServiceClient") public WebClient employeeServiceClient() {
        return WebClient.builder()
                .baseUrl(appConfig.getEmployeeServiceBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
