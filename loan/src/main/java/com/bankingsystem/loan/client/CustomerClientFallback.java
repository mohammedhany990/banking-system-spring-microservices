package com.bankingsystem.loan.client;

import com.bankingsystem.loan.dto.CustomerDto;
import com.bankingsystem.loan.helper.ApiResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerClientFallback implements CustomerClient {

    @Override
    public ApiResponse<CustomerDto> getCustomerById(Long id) {
        return
                ApiResponse.<CustomerDto>builder()
                        .success(false)
                        .message("Customer service unavailable")
                        .data(null)
                        .build();
    }

    @Override
    public ApiResponse<CustomerDto> getCustomerByUsername(String username) {
        return
                ApiResponse.<CustomerDto>builder()
                        .success(false)
                        .message("Customer service unavailable")
                        .data(null)
                        .build();
    }
}