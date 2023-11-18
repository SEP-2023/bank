package com.project.bankacquirer.repository;

import com.project.bankacquirer.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
