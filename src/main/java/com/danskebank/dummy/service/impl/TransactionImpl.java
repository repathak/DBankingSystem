package com.danskebank.dummy.service.impl;

import com.danskebank.dummy.dto.TransactionDTO;
import com.danskebank.dummy.entity.Transactions;
import com.danskebank.dummy.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDTO transactionDTO) {
    Transactions transaction = Transactions.builder()
            .transactionType(transactionDTO.getTransactionType())
            .accountNumber(transactionDTO.getAccountNumber())
            .amount(transactionDTO.getAmount())
            .status("SUCCESS")
            .build();

    transactionRepository.save(transaction);
    System.out.println("Transaction saved successfully");

    }
}
