package com.project.bankissuer.service;

import com.project.bankissuer.model.Merchant;
import com.project.bankissuer.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    public Merchant findMerchant(String id, String password){
        Merchant m = merchantRepository.findMerchantByMerchantId(id);
        if(m.getMerchantPassword().equals(password))
            return m;
        return null;
    }
}
