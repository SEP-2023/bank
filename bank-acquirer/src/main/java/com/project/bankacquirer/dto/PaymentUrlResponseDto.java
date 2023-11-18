package com.project.bankacquirer.dto;

import lombok.Data;

@Data
public class PaymentUrlResponseDto {

    private String paymentId;
    private String paymentUrl;

}
