package com.project.bankissuer.repository;

import com.project.bankissuer.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
