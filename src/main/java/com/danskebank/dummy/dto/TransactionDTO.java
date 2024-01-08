package com.danskebank.dummy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDTO {

    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;
}
