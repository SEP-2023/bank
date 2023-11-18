package com.project.bankacquirer.model;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "account")
    private Account acquirer;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "merchantOrderId")
    private String merchantOrderId;

    @Column(name = "merchantTimestamp")
    private LocalDateTime merchantTimestamp;

    @Column(name = "status")
    private TransactionStatus status;

    @Column(name = "successUrl")
    private String successUrl;

    @Column(name = "failedUrl")
    private String failedUrl;

    @Column(name = "errorUrl")
    private String errorUrl;

    @Column(name = "acquirerOrderId")
    private String acquirerOrderId;

    @Column(name = "acquirerTimestamp")
    private LocalDateTime acquirerTimestamp;

    @Column(name = "issuerOrderId")
    private String issuerOrderId;

    @Column(name = "issuerTimestamp")
    private LocalDateTime issuerTimestamp;


}
