package com.anax.account.domain.repository;

import com.anax.account.domain.model.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findAllByCustomerId(Long customerId);

    @Query("SELECT * FROM accounts WHERE id = :id FOR UPDATE")
    Mono<Account> findByIdForUpdate(Long id);
}