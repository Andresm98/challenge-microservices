package com.anax.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("customer_movement_activity")
public class CustomerMovementActivity {
    @Id
    @Column("event_id")
    private String eventId;

    @Column("movement_id")
    private Long movementId;

    @Column("customer_id")
    private Long customerId;

    @Column("account_id")
    private Long accountId;

    @Column("movement_type")
    private String movementType;

    private Double value;

    private Double balance;

    @Column("occurred_at")
    private LocalDateTime occurredAt;
}