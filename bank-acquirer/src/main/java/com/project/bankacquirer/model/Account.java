package com.project.bankacquirer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "balance")
    private Double balance;

    @OneToOne
    @JoinColumn(name = "client")
    private Client client;

    @OneToOne
    @JoinColumn(name = "creditCard")
    private CreditCard creditCard;

    @OneToMany(mappedBy = "acquirer")
    private List<Transaction> transactions;

}
