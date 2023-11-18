package com.project.bankissuer.repository;

import com.project.bankacquirer.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    public CreditCard findByPan(String pan);
}
