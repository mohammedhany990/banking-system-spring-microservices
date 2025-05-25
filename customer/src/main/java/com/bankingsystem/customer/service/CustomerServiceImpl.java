package com.bankingsystem.customer.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.customer.dto.CustomerDto;
import com.bankingsystem.customer.dto.FraudCheckResponse;
import com.bankingsystem.customer.entity.Customer;
import com.bankingsystem.customer.exception.ResourceNotFoundException;
import com.bankingsystem.customer.helper.CustomerMapper;
import com.bankingsystem.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final CustomerMapper customerMapper;
    private static final String FRAUD_CHECK_URL = "http://localhost:8081/api/v1/fraud/fraud-check/{customerId}";

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {

        if (customerRepository.existsByEmail(customerDto.getEmail())) {
            throw new IllegalArgumentException("Customer with this email already exists");
        }
        if (customerRepository.existsByUsername(customerDto.getUsername())) {
            throw new IllegalArgumentException("Customer with this username already exists");
        }
        Customer customer = customerMapper.toEntity(customerDto);

        customer = customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
                FRAUD_CHECK_URL,
                FraudCheckResponse.class,
                customer.getId());

        if (fraudCheckResponse == null) {
            throw new IllegalStateException("Fraud service did not respond");
        }

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalArgumentException("Customer is a fraudster");
        }
        return customerMapper.toDto(customer);

    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customerMapper::toDto)
                .toList();
    }

    @Override
    public CustomerDto getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toDto(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setUsername(customerDto.getUsername());
        customer.setEmail(customerDto.getEmail());
        customer.setPhone(customerDto.getPhone());
        customer.setAddress(customerDto.getAddress());
        customer.setActive(customerDto.isActive());

        Customer updatedCustomer = customerRepository.save(customer);
        
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    public CustomerDto getCustomerByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
        return customerMapper.toDto(customer);
    }

}
