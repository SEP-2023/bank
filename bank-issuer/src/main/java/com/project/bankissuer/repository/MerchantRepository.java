package com.project.bankissuer.repository;

import com.project.bankissuer.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Merchant findMerchantByMerchantId(String merchantId);
}
