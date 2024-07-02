package com.dws.challenge.dto;

import java.math.BigDecimal;

public record TransferResponse(String sourceAccountId, String destinationAccountId, BigDecimal amount,String message) {
}
