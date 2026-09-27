package com.anax.customer.domain.repository;

import com.anax.customer.domain.model.CustomerMovementActivity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerMovementActivityRepository
        extends ReactiveCrudRepository<CustomerMovementActivity, String> {

    @Modifying
    @Query("INSERT INTO customer_movement_activity " +
            "(event_id, movement_id, customer_id, account_id, movement_type, value, balance, occurred_at) " +
            "VALUES (:eventId, :movementId, :customerId, :accountId, :movementType, :value, :balance, :occurredAt) " +
            "ON CONFLICT (event_id) DO NOTHING")
    Mono<Integer> record(@Param("eventId") String eventId,
                         @Param("movementId") Long movementId,
                         @Param("customerId") Long customerId,
                         @Param("accountId") Long accountId,
                         @Param("movementType") String movementType,
                         @Param("value") Double value,
                         @Param("balance") Double balance,
                         @Param("occurredAt") java.time.LocalDateTime occurredAt);
}