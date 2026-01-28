package com.anax.account.domain.repository;

import com.anax.account.domain.model.Movement;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.time.LocalDateTime;

public interface MovementRepository extends ReactiveCrudRepository<Movement, Long> {

    @Query("SELECT m.* FROM movements m " +
            "JOIN accounts a ON m.account_id = a.id " +
            "WHERE a.customer_id = :clientId " +
            "AND m.date BETWEEN :startDate AND :endDate")
    Flux<Movement> findAllByClientIdAndDateRange(Long clientId, LocalDateTime startDate, LocalDateTime endDate);
}