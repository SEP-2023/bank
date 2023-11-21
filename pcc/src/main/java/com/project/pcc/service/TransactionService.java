package com.project.pcc.service;

import com.project.pcc.model.Transaction;
import com.project.pcc.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction findByPaymentId(String paymentId){
        return transactionRepository.findTransactionByPaymentId(paymentId);
    }

    public Transaction save(Transaction t) {
        return transactionRepository.save(t);
    }
}
