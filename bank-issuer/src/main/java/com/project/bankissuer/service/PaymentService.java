package com.project.bankissuer.service;

import com.google.zxing.NotFoundException;
import com.project.bankissuer.dto.*;
import com.project.bankissuer.model.*;
import com.project.bankissuer.repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
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

    private LoggerService logger = new LoggerService(this.getClass());

    private final String host = "http://localhost";

    private final String appUrlQrCode = host + ":4202/qr/";

    private final String appUrlCard = host + ":4202/card/";

    private final String pccUrl = host + ":8090/";

    private final String pspUrl = host + ":8086/";

    // koraci 1/2
    public PaymentUrlResponseDto getPaymentUrl(InitialRequestDto dto){
        User merchant = merchantService.findMerchant(dto.getMerchantId(), dto.getMerchantPassword());
        if(merchant == null){
            logger.error(MessageFormat.format("Merchant with ID {0} not found", dto.getMerchantId()));
            return null;
        }
        logger.info("Initiating transaction...");
        Transaction t = transactionService.initiateTransaction(dto, merchant.getAccount());
        PaymentUrlResponseDto response = new PaymentUrlResponseDto();
        response.setPaymentUrl(createPaymentUrl(t.getId().toString(), dto.isQr()));
        response.setPaymentId(t.getId().toString());
        logger.success(MessageFormat.format("Successfully created paymentUrl for transaction with ID {0}", t.getId()));
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
        System.out.println("Hashed1 intesa: " + hashPAN(dto.getPan()));

        CreditCard creditCard = creditCardRepository.findByPan(hashPAN(dto.getPan()));
        Transaction transaction = transactionService.findById(Long.parseLong(dto.getPaymentId()));
        if (transaction == null){
            logger.error(MessageFormat.format("Error while processing payment with ID {0} - invalid id", dto.getPaymentId()));
            return "Greska";
        }

        if(dto.getQr() != null && !dto.getQr().isEmpty() && (!qrCodeService.validateQRCode(dto.getQr(), transaction))){
            logger.error(MessageFormat.format("Invalid qr code for payment with ID {0}", dto.getPaymentId()));
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
        logger.info(MessageFormat.format("Processing payment (the same bank) for transaction with ID {0}", dto.getPaymentId()));
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
        logger.error(MessageFormat.format("Transaction with ID {0} failed", transaction.getId()));
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
        String issuerOrderId = generateRandomNumber(10);
        LocalDateTime issuerTimestamp = LocalDateTime.now();
        transaction.setIssuerOrderId(issuerOrderId);
        transaction.setIssuerTimestamp(issuerTimestamp);
        transactionService.save(transaction);
        logger.success(MessageFormat.format("Transaction with ID {0} successful", transaction.getId()));

        return completeTransaction(transaction);
    }

    private String completeTransaction(Transaction transaction) {
        PspRequestDto request = new PspRequestDto();
        request.setPaymentId(String.valueOf(transaction.getId()));
        request.setAcquirerTimestamp(transaction.getAcquirerTimestamp());
        request.setAcquirerOrderId(transaction.getAcquirerOrderId());
        request.setTransactionStatus(String.valueOf(transaction.getStatus()));
        request.setMerchantOrderId(transaction.getMerchantOrderId());

        logger.info(MessageFormat.format("Sending request to psp to complete transaction with ID {0}", transaction.getId()));

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
        logger.info(MessageFormat.format("Sending request to pcc to process transaction with ID {0}", request.getPaymentId()));

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
        String cvv = getCvvFromData(creditCard.getPan(), creditCard.getExpirationMonth(), creditCard.getExpirationYear());
        return  cvv.equals(dto.getSecurityCode()) &&
                creditCard.getCardholderName().equals(dto.getCardholderName()) &&
                creditCard.getExpirationYear().equals(dto.getExpirationYear()) &&
                creditCard.getExpirationMonth().equals(dto.getExpirationMonth());
    }

    public PccResponseDto processPaymentIssuer(PaymentRequestDto dto) throws NoSuchAlgorithmException {
        Transaction t = initializeTransaction(dto);
        transactionService.save(t);

        System.out.println("Hashed2 intesa: " + hashPAN(dto.getPan()));
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
                logger.error(MessageFormat.format("Transaction failed - insufficient funds - ID {0}", t.getId()));
                t.setStatus(TransactionStatus.FAILED);
                transactionService.save(t);
                response.setTransactionStatus("FAILED");
            }
        } else {
            logger.error(MessageFormat.format("Transaction failed - invalid card info - ID {0}", t.getId()));
            t.setStatus(TransactionStatus.FAILED);
            transactionService.save(t);
            response.setTransactionStatus("FAILED");
        }
        logger.success(MessageFormat.format("Successful transaction with ID {0}", t.getId()));
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

    public static String getCvvFromData(String pan, String expMonth, String expYear){
        String expirationDate = expMonth + expYear;

        try {
            String dataToHash = pan + expirationDate;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

            int cvv = Math.abs(hashBytes[0] % 1000);
            String formattedCVV = String.format("%03d", cvv);
            System.out.println("Generated CVV value: " + formattedCVV);
            return formattedCVV;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
