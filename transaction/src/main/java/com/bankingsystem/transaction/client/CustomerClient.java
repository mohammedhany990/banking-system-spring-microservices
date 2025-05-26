package com.bankingsystem.transaction.client;

import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.transaction.dto.CustomerDto;
import com.bankingsystem.transaction.helper.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomerClient {

    private final RestTemplate restTemplate;

    private final String CUSTOMER_SERVICE_URL = "http://localhost:8080/api/v1/customers";

    public ApiResponse<CustomerDto> getCustomerById(Long customerId) {
        try {
            ResponseEntity<ApiResponse<CustomerDto>> response = restTemplate
                    .exchange(
                            CUSTOMER_SERVICE_URL + "/" + customerId, HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<ApiResponse<CustomerDto>>() {
                            });

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Customer with id {} not found", customerId);
            return null;
        } catch (Exception e) {
            log.error("Failed to call customer service", e);
            throw new RuntimeException("Failed to call customer service", e);
        }

    }

    public ApiResponse<CustomerDto> getCustomerByUsername(String username) {
        try {
            ResponseEntity<ApiResponse<CustomerDto>> response = restTemplate
                    .exchange(
                            CUSTOMER_SERVICE_URL + "/username/" + username, HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<ApiResponse<CustomerDto>>() {
                            });
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call customer service", e);
        }
    }

}