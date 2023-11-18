package com.project.bankacquirer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name="merchants")
public class Merchant extends Client {

    // dobije se prilikom registracije prodavca za online prodaju
    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "merchantPassword")
    private String merchantPassword;

}
