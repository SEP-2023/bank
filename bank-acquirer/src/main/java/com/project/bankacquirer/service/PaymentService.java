package com.project.bankacquirer.service;

import com.google.zxing.NotFoundException;
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

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Random;

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

    @Autowired
    private QRCodeService qrCodeService;

    private final String appUrlQrCode = "http://localhost:4203/qr/";
    private final String appUrlCard = "http://localhost:4203/card/";
    private final String pccUrl = "http://localhost:8090/";
    private final String pspUrl = "http://localhost:8086/";

    // koraci 1/2
    public PaymentUrlResponseDto getPaymentUrl(InitialRequestDto dto){
        User merchant = merchantService.findMerchant(dto.getMerchantId(), dto.getMerchantPassword());
        if(merchant == null){
            // exception
            return null;
        }
        Transaction t = transactionService.initiateTransaction(dto, merchant.getAccount());
        PaymentUrlResponseDto response = new PaymentUrlResponseDto();
        response.setPaymentUrl(createPaymentUrl(t.getId().toString(), dto.isQr()));
        response.setPaymentId(t.getId().toString());
        return response;
    }

    public String createPaymentUrl(String id, boolean qr){
        if(qr){
            return appUrlQrCode + id;
        }
        return appUrlCard + id;
    }

    // korak 3
    public String processPayment(PaymentRequestDto dto) throws NotFoundException, IOException, NoSuchAlgorithmException {
        // hesiraj pan
        System.out.println("Hashed1: " + hashPAN(dto.getPan()));
        CreditCard creditCard = creditCardRepository.findByPan(hashPAN(dto.getPan()));
        Transaction transaction = transactionService.findById(Long.parseLong(dto.getPaymentId()));
        if (transaction == null){
            return "Greska";
        }

        if(dto.getQr() != null && !dto.getQr().isEmpty() && (!qrCodeService.validateQRCode(dto.getQr(), transaction))){
            return "Greska";
        }

        if (creditCard != null) {
            return processPaymentForSameBank(transaction, creditCard, dto);
        } else {
            return processPaymentForDifferentBank(transaction, dto);
        }
    }

    private String processPaymentForDifferentBank(Transaction transaction, PaymentRequestDto dto) {
        String acquirerOrderId = generateRandomNumber(10);
        LocalDateTime acquirerTimestamp = LocalDateTime.now();
        PccResponseDto response = createPccPaymentRequest(dto, acquirerOrderId, acquirerTimestamp, transaction.getAmount());
        transaction.setAcquirerOrderId(response.getAcquirerOrderId());
        transaction.setAcquirerTimestamp(response.getAcquirerTimestamp());
        transaction.setStatus(TransactionStatus.fromString(response.getTransactionStatus()));
        transaction.setIssuerOrderId(response.getIssuerOrderId());
        transaction.setIssuerTimestamp(response.getIssuerTimestamp());
        transactionService.save(transaction);
        return completeTransaction(transaction);
    }

    private String processPaymentForSameBank(Transaction transaction, CreditCard creditCard, PaymentRequestDto dto) {
        if (validateCreditCard(creditCard, dto)) {
            if (transaction.getStatus().equals(TransactionStatus.INITIATED)) {
                return processPaymentInitiated(transaction, creditCard);
            } else {
                return completeTransaction(transaction);
            }
        } else {
            return handleFailedTransaction(transaction);
        }
    }

    private String handleFailedTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.FAILED);
        transactionService.save(transaction);
        return completeTransaction(transaction);
    }

    private String processPaymentInitiated(Transaction transaction, CreditCard creditCard) {
        if (accountService.reserveFunds(creditCard.getAccount(), transaction.getAmount())) {
            return processSuccessfulTransaction(transaction);
        } else {
            return handleFailedTransaction(transaction);
        }
    }

    private String processSuccessfulTransaction(Transaction transaction) {
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
        if (response != null) {
            return response.getBody();
        }
        return "error";
    }

    private PccResponseDto createPccPaymentRequest(PaymentRequestDto dto, String acquirerOrderId, LocalDateTime acquirerTimestamp, Double amount) {
        PccRequestDto request = new PccRequestDto();
        request.setPaymentId(dto.getPaymentId());
        request.setAcquirerTimestamp(acquirerTimestamp);
        request.setAcquirerOrderId(acquirerOrderId);
        request.setPan(dto.getPan());
        request.setExpirationMonth(dto.getExpirationMonth());
        request.setExpirationYear(dto.getExpirationYear());
        request.setCardholderName(dto.getCardholderName());
        request.setSecurityCode(dto.getSecurityCode());
        request.setAmount(amount);
        return sendPccRequest(request);
    }

    private PccResponseDto sendPccRequest(PccRequestDto request) {
        try {
            ResponseEntity<PccResponseDto> response = WebClient.builder()
                    .build().post()
                    .uri(pccUrl + "processAcquirerRequest")
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

    public PccResponseDto processPaymentIssuer(PaymentRequestDto dto) throws NoSuchAlgorithmException {
        Transaction t = initializeTransaction(dto);
        transactionService.save(t);

        System.out.println("Hashed2: " + hashPAN(dto.getPan()));
        CreditCard creditCard = creditCardRepository.findByPan(hashPAN(dto.getPan()));
        PccResponseDto response = new PccResponseDto();
        response.setAcquirerOrderId(dto.getAcquirerOrderId());
        response.setAcquirerTimestamp(dto.getAcquirerTimestamp());

        if(creditCard != null && validateCreditCard(creditCard, dto) && t.getStatus().equals(TransactionStatus.INITIATED)){
            if(accountService.reserveFunds(creditCard.getAccount(), t.getAmount())) {
                t.setStatus(TransactionStatus.RESERVED_FUNDS);
                transactionService.save(t);
                String issuerOrderId = generateRandomNumber(10);
                LocalDateTime issuerTimestamp = LocalDateTime.now();

                t.setAcquirerOrderId(dto.getAcquirerOrderId());
                t.setAcquirerTimestamp(dto.getAcquirerTimestamp());
                t.setIssuerOrderId(issuerOrderId);
                t.setIssuerTimestamp(issuerTimestamp);
                transactionService.save(t);

                response.setIssuerTimestamp(issuerTimestamp);
                response.setIssuerOrderId(issuerOrderId);
                response.setTransactionStatus("RESERVED_FUNDS");

            } else {
                // nema dovoljno sredstava
                t.setStatus(TransactionStatus.FAILED);
                transactionService.save(t);
                response.setTransactionStatus("FAILED");
            }
        } else {
            // nevalidna kartica
            t.setStatus(TransactionStatus.FAILED);
            transactionService.save(t);
            response.setTransactionStatus("FAILED");
        }
        return response;
    }

    private Transaction initializeTransaction(PaymentRequestDto dto) {
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.INITIATED);
        transaction.setAmount(dto.getAmount());
        transaction.setAcquirerOrderId(dto.getAcquirerOrderId());
        transaction.setAcquirerTimestamp(dto.getAcquirerTimestamp());
        return transaction;
    }

    private String generateRandomNumber(int digits){
        long min = (long) Math.pow(10, digits - 1);
        long max = (long) Math.pow(10, digits) - 1;

        Random random = new Random();
        return String.valueOf(min + random.nextInt((int) (max - min + 1)));
    }

    public static String hashPAN(String panNumber) throws NoSuchAlgorithmException {
        byte[] panBytes = panNumber.getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = messageDigest.digest(panBytes);

        StringBuilder stringBuilder = new StringBuilder();
        for (byte hashedByte : hashedBytes) {
            stringBuilder.append(String.format("%02X", hashedByte));
        }

        return stringBuilder.toString();
    }
}
