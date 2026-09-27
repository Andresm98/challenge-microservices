package com.anax.customer.domain.repository;

import com.anax.customer.domain.model.CustomerOutboxEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CustomerOutboxRepository extends ReactiveCrudRepository<CustomerOutboxEvent, Long> {

    @Query("SELECT * FROM customer_event_outbox WHERE published = FALSE ORDER BY id LIMIT 100")
    Flux<CustomerOutboxEvent> findPending();
}