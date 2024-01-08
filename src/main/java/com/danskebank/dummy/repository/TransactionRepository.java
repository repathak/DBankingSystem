package com.danskebank.dummy.repository;


import com.danskebank.dummy.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository  extends JpaRepository<Transactions, String> {

}
