package com.project.pcc.service;

import com.project.pcc.dto.AcquirerRequestDto;
import com.project.pcc.dto.IssuerRequestDto;
import com.project.pcc.dto.ResponseDto;
import com.project.pcc.model.Bank;
import com.project.pcc.model.Transaction;
import com.project.pcc.model.TransactionStatus;
import com.project.pcc.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private TransactionService transactionService;

    public ResponseDto processAcquirerRequest(AcquirerRequestDto dto) {
        // provjerava zahtjev i prosljedjuje drugoj banci na osnovu pana
        String pan = dto.getPan().substring(0, 4);

        Bank bank = bankRepository.findBankByBin(pan);

        // TODO ako ne postoji, vrati gresku acquireru

        ResponseDto response = redirectToIssuer(dto, bank);
        Transaction t = new Transaction();
        t.setAcquirerTimestamp(dto.getAcquirerTimestamp());
        t.setAcquirerOrderId(dto.getAcquirerOrderId());
        t.setPaymentId(dto.getPaymentId());
        t.setAmount(dto.getAmount());
        t.setPaymentId(dto.getPaymentId());
        t.setStatus(TransactionStatus.fromString(response.getTransactionStatus()));
        transactionService.save(t);

        return response;
    }

    private ResponseDto redirectToIssuer(AcquirerRequestDto dto, Bank bank) {
        String url = bank.getUrl() + "processPaymentIssuer";
        try {
            ResponseEntity<ResponseDto> response = WebClient.builder()
                    .build().post()
                    .uri(url)
                    .body(BodyInserters.fromValue(dto))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(ResponseDto.class)
                    .block();

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}
