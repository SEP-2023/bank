package com.project.bankissuer.dto;

import lombok.Data;

@Data
public class PaymentUrlResponseDto {

    private String paymentId;
    private String paymentUrl;

}
