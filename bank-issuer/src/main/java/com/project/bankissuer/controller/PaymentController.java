package com.project.bankissuer.controller;

import com.project.bankissuer.dto.InitialRequestDto;
import com.project.bankissuer.dto.PaymentRequestDto;
import com.project.bankissuer.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/getPaymentUrl")
    public ResponseEntity<?> getPaymentUrl(InitialRequestDto dto){
        return new ResponseEntity<>(paymentService.getPaymentUrl(dto), HttpStatus.OK);
    }

    @PostMapping("/processPayment")
    public ResponseEntity<?> processPayment(PaymentRequestDto dto){
        return new ResponseEntity<>(paymentService.processPayment(dto), HttpStatus.OK);
    }
}
