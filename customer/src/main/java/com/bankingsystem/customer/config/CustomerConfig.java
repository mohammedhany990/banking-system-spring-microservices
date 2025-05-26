package com.bankingsystem.customer.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomerConfig {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/v3/api-docs")
    //                     .allowedOrigins("http://localhost:8080") // your API Gateway Swagger UI origin
    //                     .allowedMethods("GET");
    //         }
    //     };
    // }
}
