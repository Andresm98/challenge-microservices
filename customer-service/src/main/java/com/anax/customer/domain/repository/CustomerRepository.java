// Archivo: domain/repository/CustomerRepository.java
package com.anax.customer.domain.repository;

import com.anax.customer.domain.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    Mono<Customer> findByIdentification(String identification);
}