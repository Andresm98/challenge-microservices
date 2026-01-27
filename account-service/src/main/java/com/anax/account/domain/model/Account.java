package com.anax.account.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

// domain/model/Account.java
@Data
@Table("accounts")
public class Account {
    @Id
    private Long id;

    @Column("account_number")
    private String accountNumber;

    @Column("account_type")
    private String accountType;

    @Column("initial_balance")
    private Double initialBalance;

    private Boolean status;

    @Column("customer_id")
    private Long customerId;
}