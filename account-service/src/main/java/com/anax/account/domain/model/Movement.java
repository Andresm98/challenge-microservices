package com.anax.account.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

// domain/model/Movement.java
@Data
@Table("movements")
public class Movement {
    @Id
    private Long id;

    @Column("date")
    private LocalDateTime date;

    @Column("movement_type")
    private String movementType; // Retiro, Deposito

    @Column("value")
    private Double value;

    @Column("balance")
    private Double balance;

    @Column("account_id")
    private Long accountId;
}