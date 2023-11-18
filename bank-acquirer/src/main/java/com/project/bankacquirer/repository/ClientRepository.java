package com.project.bankacquirer.repository;

import com.project.bankacquirer.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByMerchantId(String id);
}
