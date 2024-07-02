package com.dws.challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "Source account ID can not be null")
        @NotBlank(message = "Source account Id can not be blank")
        String sourceAccountId,
        @NotNull(message = "Target account ID can not be null")
        @NotBlank(message = "Target account Id can not be blank")
        String destinationAccountId,
        @Positive(message = "Amount to transfer should be positive")
        BigDecimal amount) {}
