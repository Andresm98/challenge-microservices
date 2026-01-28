package com.anax.account.infrastructure.adapters.in.rest;

import com.anax.account.domain.model.Account;
import com.anax.account.domain.model.dto.UpdateAccountDTO;
import com.anax.account.domain.repository.AccountRepository;

import com.anax.account.infrastructure.adapters.out.external.CustomerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
    public Mono<Account> update(@PathVariable Long id, @RequestBody UpdateAccountDTO dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cuenta no existe")))
                .flatMap(existing -> {

                    existing.setAccountNumber(dto.getAccountNumber());
                    existing.setAccountType(dto.getAccountType());
                    existing.setStatus(dto.getStatus());
                    // existing.setCustomerId(dto.getCustomerId());  -> TODO:: en caso de que se permita cambiar el cliente
                    return repository.save(existing);
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


    @GetMapping("/jackson-test")
    public Mono<JsonNode> testJackson() {
        ObjectMapper mapper = new ObjectMapper();
        return Mono.just(mapper.createObjectNode().put("ok", true));
    }
}