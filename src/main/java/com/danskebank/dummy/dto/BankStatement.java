package com.danskebank.dummy.dto;

import com.danskebank.dummy.entity.Transactions;
import com.danskebank.dummy.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
    private static final String FILE = "C:\\Users\\Admin\\Documents\\BankStatement.pdf";
    private TransactionRepository transactionRepository;

    /**
     * Retrieve transcations within a date range given an account number
     * generate a pdf file of the transactions
     * send the file via email
     */
// Skip the first elements if list size is more than 10
    public List<Transactions> generateStatement(String accountNumber) {
/*List<Transactions> transactionsList = transactionRepository.findAll().stream().filter(transactions -> transactions.getAccountNumber().equals(accountNumber))
        .filter(transactions -> transactions.)*/

        List<Transactions> lastTenTransactions = transactionRepository.findAll().stream().filter(transactions -> transactions.getAccountNumber().equals(accountNumber))
                .skip(Math.max(0, transactionRepository.findAll().size() - 11))
                .toList();

        return lastTenTransactions;
    }


}