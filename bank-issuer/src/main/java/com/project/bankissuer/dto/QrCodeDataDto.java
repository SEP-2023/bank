package com.project.bankissuer.dto;

import lombok.Data;

@Data
public class QrCodeDataDto {
    private String account;
    private String merchantName;
    private String amount;
    private String paymentId;
}
