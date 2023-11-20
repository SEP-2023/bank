package com.project.pcc.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseDto {
    private String transactionStatus;
    private String issuerOrderId;
    private LocalDateTime issuerTimestamp;
    private String acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
}
