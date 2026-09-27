package com.anax.account.domain.repository;

import com.anax.account.domain.model.MovementOutboxEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MovementOutboxRepository extends ReactiveCrudRepository<MovementOutboxEvent, Long> {

    @Query("SELECT * FROM movement_outbox WHERE published = FALSE ORDER BY id LIMIT 100")
    Flux<MovementOutboxEvent> findPending();
}