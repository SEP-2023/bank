package com.project.bankacquirer.repository;

import com.project.bankacquirer.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
