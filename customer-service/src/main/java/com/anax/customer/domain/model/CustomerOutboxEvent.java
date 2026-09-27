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
@Table("customer_event_outbox")
public class CustomerOutboxEvent {
    @Id
    private Long id;

    @Column("event_id")
    private String eventId;

    @Column("event_type")
    private String eventType;

    @Column("customer_id")
    private Long customerId;

    private String payload;

    @Column("created_at")
    private LocalDateTime createdAt;

    private Boolean published;
}