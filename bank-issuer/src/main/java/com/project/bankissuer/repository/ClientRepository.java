package com.project.bankissuer.repository;

import com.project.bankissuer.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
