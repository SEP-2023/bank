package com.project.pcc.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name="transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "status")
    private TransactionStatus status;

    private String paymentId;
    @Column(name = "acquirerOrderId")
    private String acquirerOrderId;

    @Column(name = "acquirerTimestamp")
    private LocalDateTime acquirerTimestamp;

    @Column(name = "issuerOrderId")
    private String issuerOrderId;

    @Column(name = "issuerTimestamp")
    private LocalDateTime issuerTimestamp;


}
