package com.bankingsystem.bankaccount.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.bankaccount.dto.CustomerDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomerClient {

    private final RestTemplate restTemplate;

    private final String CUSTOMER_SERVICE_URL = "http://localhost:8080/api/v1/customers";

    public CustomerDto getCustomerById(Long customerId) {
        try {
            return restTemplate.getForObject(CUSTOMER_SERVICE_URL + "/" + customerId, CustomerDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            // Customer not found
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call customer service", e);
        }
    }

}