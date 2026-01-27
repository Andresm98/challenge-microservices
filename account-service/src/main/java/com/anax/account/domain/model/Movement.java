package com.anax.account.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

// domain/model/Movement.java
@Data
@Table("movements")
public class Movement {
    @Id
    private Long id;
    private LocalDateTime date;
    private String movementType; // Retiro, Deposito
    private Double value;
    private Double balance;
    private Long accountId;
}