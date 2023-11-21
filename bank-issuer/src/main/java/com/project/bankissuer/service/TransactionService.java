package com.project.bankissuer.service;

import com.project.bankissuer.dto.InitialRequestDto;
import com.project.bankissuer.model.*;
import com.project.bankissuer.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {


    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction initiateTransaction(InitialRequestDto dto, Account account) {
        Transaction t = new Transaction();
        t.setStatus(TransactionStatus.INITIATED);
        t.setAmount(dto.getAmount());
        t.setMerchantTimestamp(dto.getMerchantTimestamp());
        t.setMerchantOrderId(dto.getMerchantOrderId());
        t.setSuccessUrl(dto.getSuccessUrl());
        t.setFailedUrl(dto.getFailedUrl());
        t.setErrorUrl(dto.getErrorUrl());
        t.setAcquirer(account);
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
