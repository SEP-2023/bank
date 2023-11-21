package com.project.bankissuer.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="users")
@Inheritance(strategy=TABLE_PER_CLASS)
public class User {

    @Id
    @SequenceGenerator(name = "userSeqGen", sequenceName = "userSeq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeqGen")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @OneToOne
    @JoinColumn(name = "account")
    private Account account;

}
