package com.bankingsystem.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor      
@AllArgsConstructor
public class CreateNotificationDto {

    @NotNull(message = "Customer ID must not be null")
    private Long customerId;

    @NotBlank(message = "Customer Email must not be blank")
    private String customerEmail;

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Message must not be blank")
    private String message;

    @NotNull(message = "Notification type must not be null")
    private String type;
}
