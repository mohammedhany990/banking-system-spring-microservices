package com.bankingsystem.bankaccount.client;

import com.bankingsystem.bankaccount.dto.CustomerDto;
import com.bankingsystem.bankaccount.helper.ApiResponse;
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

/*

public class CustomerClient {

    private final WebClient webClient;

    @Value("${customer.service.url}")
    private final String customerServiceUrl;

    public Mono<ApiResponse<CustomerDto>> getCustomerById(Long customerId) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(customerServiceUrl + "/{id}").build(customerId))
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    log.error("Customer service error: {}", response.statusCode());
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("No error body")
                            .flatMap(body -> Mono
                                    .error(new RuntimeException("Customer service error: " + response.statusCode())));
                })
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDto>>() {
                })
                .doOnSuccess(response -> log.info("Fetched customer with id {}", customerId))
                .doOnError(e -> log.error("Failed to fetch customer by id {}", customerId, e));
    }

    public ApiResponse<CustomerDto> getCustomerByUsername(String username) {
        try {
            return webClient.get()
                    .uri(customerServiceUrl + "/username/" + username)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDto>>() {
                    })
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Customer with username {} not found", username);
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call customer service", e);
        }
    }

}

 */