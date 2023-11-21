package com.project.pcc.repository;

import com.project.pcc.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public Transaction findTransactionByPaymentId(String paymentId);
}
