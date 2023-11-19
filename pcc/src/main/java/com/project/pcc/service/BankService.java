package com.project.pcc.service;

import com.project.pcc.dto.AcquirerRequestDto;
import com.project.pcc.dto.IssuerRequestDto;
import com.project.pcc.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    public boolean processAcquirerRequest(AcquirerRequestDto dto) {
        // provjerava zahtjev i prosljedjuje drugoj banci na osnovu pana
        return false;
    }

    public boolean processIssuerPayment(IssuerRequestDto dto) {
        // odgovor kupca prosljedjuje sada banci prodavca
        return false;
    }
}
