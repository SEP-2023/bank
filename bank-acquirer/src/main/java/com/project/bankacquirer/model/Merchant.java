package com.project.bankacquirer.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="merchants")
public class Merchant extends Client {

    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "merchantPassword")
    private String merchantPassword;

}
