package com.project.pcc.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AcquirerRequestDto {

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
