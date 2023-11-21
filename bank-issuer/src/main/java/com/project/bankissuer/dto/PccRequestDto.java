package com.project.bankissuer.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PccRequestDto {

    private String pan;
    private String securityCode;
    private String cardholderName;
    private String expirationMonth;
    private String expirationYear;
    private String paymentId;
    private String acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private Double amount;

}
