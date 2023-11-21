package com.project.bankacquirer.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PccResponseDto {

    private String transactionStatus;
    private String acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private String issuerOrderId;
    private LocalDateTime issuerTimestamp;
}
