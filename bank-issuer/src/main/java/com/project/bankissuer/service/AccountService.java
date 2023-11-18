package com.project.bankissuer.service;

import com.project.bankissuer.model.Account;
import com.project.bankissuer.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public boolean reserveFunds(Account account, double amount){
        if(account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    public void transferFunds(Account acquirer, Double amount) {
        acquirer.setBalance(acquirer.getBalance() + amount);
        accountRepository.save(acquirer);
    }
}
