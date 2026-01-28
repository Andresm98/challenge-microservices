package com.anax.account.infrastructure.adapters.in.rest;

import com.anax.account.domain.model.Account;
import com.anax.account.domain.repository.AccountRepository;

import com.anax.account.infrastructure.adapters.out.external.CustomerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/accounts") // limpiar los métodos
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository repository;

    @GetMapping
    public Flux<Account> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Account> getById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Account> create(@RequestBody Account account) {
        return repository.save(account);
    }

    @PutMapping("/{id}")
    public Mono<Account> update(@PathVariable Long id, @RequestBody Account account) {
        return repository.findById(id)
                .flatMap(existing -> {
                    // Consistencia del ID
                    account.setId(existing.getId());
                    return repository.save(account);
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return repository.deleteById(id);
    }


    @Autowired
    private CustomerClient customerClient;
    @GetMapping("/test-customer/{id}")
    public Mono<String> testWebClient(@PathVariable Long id) {
        return customerClient.getCustomerName(id)
                .map(name -> "(WebClient) ========> Conexión exitosa. El cliente que usted llamó es: " + name);
    }
}