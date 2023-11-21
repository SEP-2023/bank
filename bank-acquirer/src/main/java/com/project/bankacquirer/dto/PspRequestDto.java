package com.project.bankacquirer.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PspRequestDto {

    private String transactionStatus;

    private String acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private String paymentId;
    private String merchantOrderId;
}
