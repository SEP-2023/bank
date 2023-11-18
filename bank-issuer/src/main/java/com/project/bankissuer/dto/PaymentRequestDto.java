package com.project.bankissuer.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private String pan;
    private String securityCode;
    private String cardholderName;
    private String expirationMonth;
    private String expirationYear;
    private String paymentId;
}
