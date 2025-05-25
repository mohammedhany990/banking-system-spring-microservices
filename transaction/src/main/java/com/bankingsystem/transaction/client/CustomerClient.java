package com.bankingsystem.transaction.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.transaction.dto.CustomerDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomerClient {

    private final RestTemplate restTemplate;

    private final String CUSTOMER_SERVICE_URL = "http://localhost:8080/api/v1/customers";

    public CustomerDto getCustomerById(Long customerId) {
        try {
            return restTemplate.getForObject(CUSTOMER_SERVICE_URL + "/" + customerId, CustomerDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Customer with id {} not found", customerId);
            return null;
        } catch (Exception e) {
            log.error("Failed to call customer service", e);
            throw new RuntimeException("Failed to call customer service", e);
        }

    }

    public CustomerDto getCustomerByUsername(String username) {
        try {
            return restTemplate.getForObject(CUSTOMER_SERVICE_URL + "/username/" + username, CustomerDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call customer service", e);
        }
    }

}