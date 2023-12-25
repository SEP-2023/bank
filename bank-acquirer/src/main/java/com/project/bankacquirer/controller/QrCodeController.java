package com.project.bankacquirer.controller;

import com.project.bankacquirer.dto.InitialRequestDto;
import com.project.bankacquirer.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class QrCodeController {

    @Autowired
    QRCodeService qrCodeService;

    @GetMapping("/getQrCode/{id}")
    public ResponseEntity<?> getQrCode(@PathVariable String id){
        return new ResponseEntity<>(qrCodeService.generateQRCode(id), HttpStatus.OK);
    }
}
