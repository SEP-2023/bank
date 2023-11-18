package com.project.bankissuer.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InitialRequestDto {
    private String merchantId;
    private String merchantPassword;
    private Double amount;
    private String merchantOrderId;
    private LocalDateTime merchantTimestamp;
    private String successUrl;
    private String failedUrl;
    private String errorUrl;

}