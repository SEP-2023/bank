package com.project.bankissuer.controller;

import com.project.bankacquirer.dto.InitialRequestDto;
import com.project.bankacquirer.dto.PaymentRequestDto;
import com.project.bankacquirer.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/getPaymentUrl")
    public ResponseEntity<?> getPaymentUrl(InitialRequestDto dto){
        return null;
    }

    @PostMapping("/processPayment")
    public ResponseEntity<?> processPayment(PaymentRequestDto dto){
        return null;
    }
}
