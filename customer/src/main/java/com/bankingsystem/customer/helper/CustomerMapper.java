package com.bankingsystem.customer.helper;

import org.springframework.stereotype.Component;

import com.bankingsystem.customer.dto.CustomerDto;
import com.bankingsystem.customer.entity.Customer;

@Component
public class CustomerMapper {

    public CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .active(customer.isActive())
                .build();
    }

    public Customer toEntity(CustomerDto dto) {
        return Customer.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .active(dto.isActive())
                .build();
    }
}