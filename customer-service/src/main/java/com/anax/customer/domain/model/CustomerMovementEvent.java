package com.anax.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMovementEvent {
    private String eventId;
    private Long movementId;
    private Long customerId;
    private Long accountId;
    private String movementType;
    private Double value;
    private Double balance;
    private String occurredAt;
}