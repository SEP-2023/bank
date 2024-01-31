package com.project.bankissuer.controller;

import com.google.zxing.NotFoundException;
import com.project.bankissuer.dto.InitialRequestDto;
import com.project.bankissuer.dto.PaymentRequestDto;
import com.project.bankissuer.service.LoggerService;
import com.project.bankissuer.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    private LoggerService logger = new LoggerService(this.getClass());

    @PostMapping("/getPaymentUrl")
    public ResponseEntity<?> getPaymentUrl(@RequestBody InitialRequestDto dto){
        logger.info("Acquiring paymentUrl from merchant's bank");
        return new ResponseEntity<>(paymentService.getPaymentUrl(dto), HttpStatus.OK);
    }

    @PostMapping("/processPayment")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequestDto dto) throws NotFoundException, IOException, NoSuchAlgorithmException {
        logger.info(MessageFormat.format("Processing payment with ID {0}", dto.getPaymentId()));
        return new ResponseEntity<>(paymentService.processPayment(dto), HttpStatus.OK);
    }

    @PostMapping("/processPaymentIssuer")
    public ResponseEntity<?> processPaymentIssuer(@RequestBody PaymentRequestDto dto) throws NoSuchAlgorithmException {
        logger.info(MessageFormat.format("Issuer processing payment with ID {0}", dto.getPaymentId()));
        return new ResponseEntity<>(paymentService.processPaymentIssuer(dto), HttpStatus.OK);
    }
}
