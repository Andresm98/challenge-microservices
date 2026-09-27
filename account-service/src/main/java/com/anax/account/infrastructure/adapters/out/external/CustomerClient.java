package com.anax.account.infrastructure.adapters.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.anax.account.domain.model.CachedCustomer;
import com.anax.account.domain.model.dto.CustomerProfileDTO;
import com.anax.account.domain.repository.CustomerCacheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class CustomerClient {
    private final WebClient webClient;
    private final CustomerCacheRepository customerCacheRepository;
    private final CircuitBreaker circuitBreaker;
    private final io.github.resilience4j.retry.Retry retry;
    private final Duration timeout;

    public CustomerClient(WebClient.Builder webClientBuilder,
                          CustomerCacheRepository customerCacheRepository,
                          CircuitBreaker customerCircuitBreaker,
                          io.github.resilience4j.retry.Retry customerRetry,
                          @Value("${customer.url:http://localhost:8081/api/v1/customers}") String baseUrl,
                          @Value("${customer.timeout:2s}") Duration timeout) {
        this.customerCacheRepository = customerCacheRepository;
        this.circuitBreaker = customerCircuitBreaker;
        this.retry = customerRetry;
        this.timeout = timeout;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<String> getCustomerName(Long id) {
        Mono<String> remoteLookup = webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(String.class) //
                .switchIfEmpty(Mono.error(new IllegalStateException("customer-service devolvió una respuesta vacía")))
                .map(this::extractName)
                .timeout(timeout)
                .flatMap(name -> customerCacheRepository.upsertName(id, name).thenReturn(name))
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));

        return remoteLookup
                .onErrorResume(error -> {
                    log.warn("customer-service no disponible; intentando cache local ({})", error.getClass().getSimpleName());
                    return customerCacheRepository.findById(id)
                            .map(CachedCustomer::getName)
                            .defaultIfEmpty("Cliente Desconocido");
                });
    }

            public Mono<List<CustomerProfileDTO>> getAllCustomers() {
            Mono<List<CustomerProfileDTO>> remoteLookup = webClient.get()
                .uri("")
                .retrieve()
                .bodyToFlux(CustomerProfileDTO.class)
                .collectList()
                .timeout(timeout)
                .flatMap(customers -> Flux.fromIterable(customers)
                    .concatMap(this::cacheCustomer)
                    .then(Mono.just(customers)))
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));

            return remoteLookup
                .onErrorResume(error -> {
                    log.warn("customer-service no disponible; usando perfiles cacheados ({})",
                        error.getClass().getSimpleName());
                    return customerCacheRepository.findAll()
                        .map(this::toCustomerProfile)
                        .collectList();
                });
            }

            private CustomerProfileDTO toCustomerProfile(CachedCustomer customer) {
            return new CustomerProfileDTO(
                customer.getId(), customer.getName(), customer.getGender(), customer.getAge(),
                customer.getIdentification(), customer.getAddress(), customer.getPhone(), customer.getStatus());
            }

    private Mono<Integer> cacheCustomer(CustomerProfileDTO customer) {
        return customerCacheRepository.upsert(
                customer.getId(), customer.getName(), customer.getGender(), customer.getAge(),
                customer.getIdentification(), customer.getAddress(), customer.getPhone(), customer.getStatus());
    }

    private String extractName(String body) {
        try {
            JsonNode node = new ObjectMapper().readTree(body);
            String name = node.path("name").asText();
            if (name.isBlank()) {
                throw new IllegalStateException("customer-service devolvió un nombre vacío");
            }
            return name;
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo interpretar la respuesta de customer-service", exception);
        }
    }
}