package com.project.bankacquirer.repository;

import com.project.bankacquirer.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Merchant findMerchantByMerchantId(String merchantId);
}
