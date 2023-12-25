package com.project.bankissuer.controller;

import com.project.bankissuer.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QrCodeController {

    @Autowired
    QRCodeService qrCodeService;

    @GetMapping("/getQrCode/{id}")
    public ResponseEntity<?> getQrCode(@PathVariable String id){
        return new ResponseEntity<>(qrCodeService.generateQRCode(id), HttpStatus.OK);
    }
}
