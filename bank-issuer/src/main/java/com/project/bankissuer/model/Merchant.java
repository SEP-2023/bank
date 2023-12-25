package com.project.bankissuer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="merchants")
public class Merchant extends User {

    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "merchantPassword")
    private String merchantPassword;

}
