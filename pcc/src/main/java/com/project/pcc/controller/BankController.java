package com.project.pcc.controller;

import com.project.pcc.dto.AcquirerRequestDto;
import com.project.pcc.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankController {

    @Autowired
    private BankService bankService;

    @PostMapping("/processAcquirerRequest")
    public ResponseEntity<?> processAcquirerRequest(@RequestBody AcquirerRequestDto dto){
        return new ResponseEntity<>(bankService.processAcquirerRequest(dto), HttpStatus.OK);
    }

}
