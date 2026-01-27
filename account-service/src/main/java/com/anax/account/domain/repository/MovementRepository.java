package com.anax.account.domain.repository;

import com.anax.account.domain.model.Movement;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface MovementRepository extends ReactiveCrudRepository<Movement, Long> {
    Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDateTime start, LocalDateTime end);
}