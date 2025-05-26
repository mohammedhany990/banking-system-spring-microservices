package com.bankingsystem.apigateway;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi customer() {
        return GroupedOpenApi.builder()
                .group("customer")
                .pathsToMatch("/customer/**")
                .build();
    }

    @Bean
    public GroupedOpenApi card() {
        return GroupedOpenApi.builder()
                .group("card")
                .pathsToMatch("/card/**")
                .build();
    }
}
