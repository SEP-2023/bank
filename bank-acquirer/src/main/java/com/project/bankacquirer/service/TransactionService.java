package com.project.bankacquirer.service;

import com.project.bankacquirer.dto.InitialRequestDto;
import com.project.bankacquirer.model.*;
import com.project.bankacquirer.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

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
        String acquirerOrderId = generateRandomNumber(10);
        LocalDateTime acquirerTimestamp = LocalDateTime.now();
        t.setAcquirerOrderId(acquirerOrderId);
        t.setAcquirerTimestamp(acquirerTimestamp);

        return transactionRepository.save(t);
    }

    private String generateRandomNumber(int digits){
        long min = (long) Math.pow(10, digits - 1);
        long max = (long) Math.pow(10, digits) - 1;

        Random random = new Random();
        return String.valueOf(min + random.nextInt((int) (max - min + 1)));
    }

    public Transaction findById(Long transactionId) {
        return transactionRepository.findById(transactionId).orElse(null);
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
