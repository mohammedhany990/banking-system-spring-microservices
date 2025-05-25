package com.bankingsystem.customer.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankingsystem.customer.dto.CustomerDto;
import com.bankingsystem.customer.helper.ApiResponse;
import com.bankingsystem.customer.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerDto createdCustomer = customerService.createCustomer(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CustomerDto>builder()
                        .success(true)
                        .message("Customer created successfully")
                        .data(createdCustomer)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.<List<CustomerDto>>builder()
                .success(true)
                .message("Customers retrieved successfully")
                .data(customers)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable Long id) {
        CustomerDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.<CustomerDto>builder()
                .success(true)
                .message("Customer retrieved successfully")
                .data(customer)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(@PathVariable Long id,
            @RequestBody CustomerDto customerDto) {
        CustomerDto updatedCustomer = customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok(ApiResponse.<CustomerDto>builder()
                .success(true)
                .message("Customer updated successfully")
                .data(updatedCustomer)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Customer deleted successfully")
                .data(null)
                .build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerByUsername(@PathVariable String username) {
        CustomerDto customer = customerService.getCustomerByUsername(username);
        return ResponseEntity.ok(ApiResponse.<CustomerDto>builder()
                .success(true)
                .message("Customer retrieved successfully")
                .data(customer)
                .build());
    }
}