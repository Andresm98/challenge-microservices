package com.anax.account.domain.repository;

import com.anax.account.domain.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    Mono<Account> findByAccountNumber(String accountNumber);
}