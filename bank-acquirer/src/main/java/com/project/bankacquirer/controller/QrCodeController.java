package com.project.bankacquirer.controller;

import com.project.bankacquirer.dto.InitialRequestDto;
import com.project.bankacquirer.service.LoggerService;
import com.project.bankacquirer.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
public class QrCodeController {

    @Autowired
    QRCodeService qrCodeService;

    private LoggerService logger = new LoggerService(this.getClass());

    @GetMapping("/getQrCode/{id}")
    public ResponseEntity<?> getQrCode(@PathVariable String id){
        logger.info(MessageFormat.format("Acquiring qr code with ID {0}", id));
        return new ResponseEntity<>(qrCodeService.generateQRCode(id), HttpStatus.OK);
    }
}
