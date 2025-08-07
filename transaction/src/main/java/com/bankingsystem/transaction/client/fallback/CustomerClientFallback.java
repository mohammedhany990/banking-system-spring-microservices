package com.bankingsystem.card.client.fallback;


import com.bankingsystem.card.client.CustomerClient;
import com.bankingsystem.card.dto.transactions.CustomerDto;
import com.bankingsystem.card.helper.ApiResponse;
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
