package com.anax.account.domain.model;

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
@Table("movement_outbox")
public class MovementOutboxEvent {
    @Id
    private Long id;

    @Column("movement_id")
    private Long movementId;

    @Column("created_at")
    private LocalDateTime createdAt;

    private Boolean published;
}