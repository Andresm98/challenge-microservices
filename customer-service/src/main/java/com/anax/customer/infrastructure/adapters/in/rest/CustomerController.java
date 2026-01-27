// Archivo: infrastructure/adapters/in/rest/CustomerController.java
package com.anax.customer.infrastructure.adapters.in.rest;

import com.anax.customer.domain.model.Customer;
import com.anax.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor // Inyección por constructor automática
public class CustomerController {

    private final CustomerRepository repository;

    @GetMapping
    public Flux<Customer> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Customer> getById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> create(@RequestBody Customer customer) {
        return repository.save(customer);
    }

    @PutMapping("/{id}")
    public Mono<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
        return repository.findById(id)
                .flatMap(existing -> {
                    customer.setId(id);
                    return repository.save(customer);
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return repository.deleteById(id);
    }
}