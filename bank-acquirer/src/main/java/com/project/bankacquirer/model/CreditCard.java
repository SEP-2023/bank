package com.project.bankacquirer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name="credit_cards")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "pan")
    private String pan;

    @Column(name = "securityCode")
    private String securityCode;

    @Column(name = "cardholderName")
    private String cardholderName;

    @Column(name = "expirationMonth")
    private String expirationMonth;

    @Column(name = "expirationYear")
    private String expirationYear;

    @OneToOne
    @JoinColumn(name = "account")
    private Account account;


}
