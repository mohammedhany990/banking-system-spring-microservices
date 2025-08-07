package com.bankingsystem.loan.client;

import com.bankingsystem.loan.dto.CustomerDto;
import com.bankingsystem.loan.helper.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "customer-service", fallback = CustomerClientFallback.class)
public interface CustomerClient {

    @GetMapping("/api/v1/customers/{id}")
    ApiResponse<CustomerDto> getCustomerById(@PathVariable Long id);


    @GetMapping("/api/v1/customers/username/{username}")
    ApiResponse<CustomerDto> getCustomerByUsername(@PathVariable String username);

}