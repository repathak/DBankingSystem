package com.danskebank.dummy.controller;

import com.danskebank.dummy.dto.BankStatement;
import com.danskebank.dummy.entity.Transactions;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {

    private BankStatement bankStatement;
@GetMapping
    public List<Transactions> generateBankStatement(@RequestParam String accountNumber){

        return bankStatement.generateStatement(accountNumber);
    }
}
