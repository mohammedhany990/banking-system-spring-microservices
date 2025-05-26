package com.bankingsystem.transaction.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDateRangeRequest {
    @NotNull
    private LocalDateTime from;

    @NotNull
    private LocalDateTime to;
}
