package com.project.bankacquirer.service;

import com.project.bankacquirer.dto.*;
import com.project.bankacquirer.model.*;
import com.project.bankacquirer.repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountService accountService;

    private final String appUrl = "http://localhost:4203/";
    private final String pccUrl = "http://localhost:8901/";

    private final String pspUrl = "http://localhost:8086/";

    // koraci 1/2
    public PaymentUrlResponseDto getPaymentUrl(InitialRequestDto dto){
        System.out.println(dto.getMerchantPassword());
        Client merchant = merchantService.findMerchant(dto.getMerchantId(), dto.getMerchantPassword());
        if(merchant == null){
            // exception
            return null;
        }
        Transaction t = transactionService.initiateTransaction(dto, merchant.getAccount());
        PaymentUrlResponseDto response = new PaymentUrlResponseDto();
        response.setPaymentUrl(createPaymentUrl(t.getId().toString()));
        response.setPaymentId(t.getId().toString());
        return response;
    }

    public String createPaymentUrl(String id){
        return appUrl + id;
    }

    // korak 3
    public String processPayment(PaymentRequestDto dto){
        CreditCard creditCard = creditCardRepository.findByPan(dto.getPan());
        Transaction transaction = transactionService.findById(Long.parseLong(dto.getPaymentId()));
        if (transaction == null){
            // vrati gresku
            return "Greska";
        }

        // ako je issuer iz iste banke
        if(creditCard != null){
            if(validateCreditCard(creditCard, dto)){
                if(transaction.getStatus().equals(TransactionStatus.INITIATED)) {
                    if(accountService.reserveFunds(creditCard.getAccount(), transaction.getAmount())) {
                        transaction.setStatus(TransactionStatus.RESERVED_FUNDS);
                        transactionService.save(transaction);

                        Account acquirer = transaction.getAcquirer();
                        accountService.transferFunds(acquirer, transaction.getAmount());
                        transaction.setStatus(TransactionStatus.SUCCESSFUL);
                        String acquirerOrderId = "randomNumberDuzine10";
                        LocalDateTime acquirerTimestamp = LocalDateTime.now();
                        transaction.setAcquirerOrderId(acquirerOrderId);
                        transaction.setAcquirerTimestamp(acquirerTimestamp);
                        transactionService.save(transaction);
                    } else {
                        // nema dovoljno sredstava
                        transaction.setStatus(TransactionStatus.FAILED);
                        transactionService.save(transaction);
                    }
                }
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
                transactionService.save(transaction);
                // vrati nesto
            }
        } else {
            // ako issuer nije iz iste banke
            // ako ne postoji - generisi ACQUIRER_ORDER_ID i ACQUIRER_TIMESTAMP i salji na pcc zahtjev zajedno s podacima o kartici
            String acquirerOrderId = "randomNumberDuzine10";
            LocalDateTime acquirerTimestamp = LocalDateTime.now();
            PccResponseDto response = createPccPaymentRequest(dto, acquirerOrderId, acquirerTimestamp);
            transaction.setAcquirerOrderId(acquirerOrderId);
            transaction.setAcquirerTimestamp(acquirerTimestamp);
            if(response == null){
                transaction.setStatus(TransactionStatus.FAILED);
                transactionService.save(transaction);
            } else {
                transaction.setStatus(TransactionStatus.fromString(response.getTransactionStatus()));
                transaction.setAcquirerOrderId(response.getAcquirerOrderId());
                transaction.setAcquirerTimestamp(response.getAcquirerTimestamp());
                transaction.setIssuerOrderId(response.getIssuerOrderId());
                transaction.setIssuerTimestamp(response.getIssuerTimestamp());
                transactionService.save(transaction);
            }
            // ...
        }

        return completeTransaction(transaction);
    }

    private String completeTransaction(Transaction transaction) {
        PspRequestDto request = new PspRequestDto();
        request.setPaymentId(String.valueOf(transaction.getId()));
        request.setAcquirerTimestamp(transaction.getAcquirerTimestamp());
        request.setAcquirerOrderId(transaction.getAcquirerOrderId());
        request.setTransactionStatus(String.valueOf(transaction.getStatus()));
        request.setMerchantOrderId(transaction.getMerchantOrderId());

        ResponseEntity<String> response = WebClient.builder()
                .build().post()
                .uri(pspUrl + "completeTransaction")
                .body(BodyInserters.fromValue(request))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block();
        return response.getBody();
    }

    private PccResponseDto createPccPaymentRequest(PaymentRequestDto dto, String acquirerOrderId, LocalDateTime acquirerTimestamp) {

        PccRequestDto request = new PccRequestDto();
        request.setPaymentId(dto.getPaymentId());
        request.setAcquirerTimestamp(acquirerTimestamp);
        request.setAcquirerOrderId(acquirerOrderId);
        request.setPan(dto.getPan());
        request.setExpirationMonth(dto.getExpirationMonth());
        request.setExpirationYear(dto.getExpirationYear());
        request.setCardholderName(dto.getCardholderName());
        request.setSecurityCode(dto.getSecurityCode());

        return sendPccRequest(request);
    }

    private PccResponseDto sendPccRequest(PccRequestDto request) {
        try {
            ResponseEntity<PccResponseDto> response = WebClient.builder()
                    .build().post()
                    .uri(pccUrl)
                    .body(BodyInserters.fromValue(request))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(PccResponseDto.class)
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

    private boolean validateCreditCard(CreditCard creditCard, PaymentRequestDto dto) {
        return  creditCard.getSecurityCode().equals(dto.getSecurityCode()) &&
                creditCard.getCardholderName().equals(dto.getCardholderName()) &&
                creditCard.getExpirationYear().equals(dto.getExpirationYear()) &&
                creditCard.getExpirationMonth().equals(dto.getExpirationMonth());
    }

    public boolean processPaymentIssuer(PaymentRequestDto dto) {
        return false;
    }
}
