package com.bankingsystem.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.customer.client.NotificationClient;
import com.bankingsystem.customer.dto.CreateNotificationDto;
import com.bankingsystem.customer.dto.CustomerDto;
import com.bankingsystem.customer.entity.Customer;
import com.bankingsystem.customer.exception.CustomerAlreadyExistsException;
import com.bankingsystem.customer.exception.CustomerNotFoundException;

import com.bankingsystem.customer.helper.CustomerMapper;
import com.bankingsystem.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final NotificationClient notificationClient;

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {

        if (customerRepository.existsByEmail(customerDto.getEmail())) {
            throw new CustomerAlreadyExistsException("Customer with this email already exists");
        }
        if (customerRepository.existsByUsername(customerDto.getUsername())) {
            throw new CustomerAlreadyExistsException("Customer with this username already exists");
        }
        Customer customer = customerMapper.toEntity(customerDto);
        
        customer.setActive(true);

        customer = customerRepository.saveAndFlush(customer);

        try {
            notificationClient.sendNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Welcome to Our Platform!\n\n")
                    .type("GENERAL")
                    .message("Hello " + customer.getUsername()
                            + ", your account has been successfully created. Enjoy our services!")
                    .build());

            log.info("Notification sent successfully for customer id {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send notification for customer id {}", customer.getId(), e);
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
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
        return customerMapper.toDto(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));

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
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with username: " + username));
        return customerMapper.toDto(customer);
    }

}
