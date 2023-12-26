package com.project.bankacquirer.controller;

import com.google.zxing.NotFoundException;
import com.project.bankacquirer.dto.InitialRequestDto;
import com.project.bankacquirer.dto.PaymentRequestDto;
import com.project.bankacquirer.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/getPaymentUrl")
    public ResponseEntity<?> getPaymentUrl(@RequestBody InitialRequestDto dto){
        return new ResponseEntity<>(paymentService.getPaymentUrl(dto), HttpStatus.OK);
    }

    @PostMapping("/processPayment")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequestDto dto) throws NotFoundException, IOException, NoSuchAlgorithmException {
        return new ResponseEntity<>(paymentService.processPayment(dto), HttpStatus.OK);
    }

    @PostMapping("/processPaymentIssuer")
    public ResponseEntity<?> processPaymentIssuer(@RequestBody PaymentRequestDto dto) throws NoSuchAlgorithmException {
        return new ResponseEntity<>(paymentService.processPaymentIssuer(dto), HttpStatus.OK);
    }
}
