package com.anax.customer.application;

import com.anax.customer.domain.model.Customer;
import com.anax.customer.domain.model.CustomerChangedEvent;
import com.anax.customer.domain.model.CustomerOutboxEvent;
import com.anax.customer.domain.repository.CustomerOutboxRepository;
import com.anax.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerApplicationService {

    private final CustomerRepository customerRepository;
    private final CustomerOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Mono<Customer> create(Customer customer) {
        customer.setId(null);
        return customerRepository.save(customer)
                .flatMap(saved -> enqueueEvent(saved, "CREATED").thenReturn(saved));
    }

    @Transactional
    public Mono<Customer> update(Long id, Customer customer) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no existe")))
                .flatMap(existing -> {
                    customer.setId(id);
                    return customerRepository.save(customer)
                            .flatMap(saved -> enqueueEvent(saved, "UPDATED").thenReturn(saved));
                });
    }

    @Transactional
    public Mono<Void> delete(Long id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no existe")))
                .flatMap(customer -> customerRepository.deleteById(id)
                        .then(enqueueEvent(customer, "DELETED")));
    }

    private Mono<Void> enqueueEvent(Customer customer, String eventType) {
        String eventId = UUID.randomUUID().toString();
        CustomerChangedEvent event = new CustomerChangedEvent(
                eventId,
                eventType,
                customer.getId(),
                customer.getName(),
                customer.getGender(),
                customer.getAge(),
                customer.getIdentification(),
                customer.getAddress(),
                customer.getPhone(),
                customer.getStatus(),
                Instant.now().toString());

        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(payload -> outboxRepository.save(new CustomerOutboxEvent(
                        null, eventId, eventType, customer.getId(), payload, LocalDateTime.now(), false)))
                .then();
    }
}