package com.bankingsystem.customer.service;

import java.util.List;

import com.bankingsystem.customer.dto.CustomerDto;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto customerDto);
    List<CustomerDto> getAllCustomers();
    CustomerDto getCustomerById(Long id);
    void deleteCustomer(Long id);
    CustomerDto updateCustomer(Long id, CustomerDto customerDto);
    CustomerDto getCustomerByUsername(String username);
}
