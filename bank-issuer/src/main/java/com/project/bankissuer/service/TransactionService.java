package com.project.bankissuer.service;

import com.project.bankacquirer.dto.InitialRequestDto;
import com.project.bankacquirer.model.Transaction;
import com.project.bankacquirer.model.TransactionStatus;
import com.project.bankacquirer.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {


    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction initiateTransaction(InitialRequestDto dto) {
        Transaction t = new Transaction();
        t.setStatus(TransactionStatus.INITIATED);
        t.setAmount(dto.getAmount());
        t.setMerchantTimestamp(dto.getMerchantTimestamp());
        t.setMerchantOrderId(dto.getMerchantOrderId());
        t.setSuccessUrl(dto.getSuccessUrl());
        t.setFailedUrl(dto.getFailedUrl());
        t.setErrorUrl(dto.getErrorUrl());
        // ...
        return transactionRepository.save(t);
    }

    public Transaction findById(Long transactionId) {
        return transactionRepository.findById(transactionId).orElse(null);
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
