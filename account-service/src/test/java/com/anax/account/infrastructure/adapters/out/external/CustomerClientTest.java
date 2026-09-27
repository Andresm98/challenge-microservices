package com.anax.account.infrastructure.adapters.out.external;

import com.anax.account.domain.model.CachedCustomer;
import com.anax.account.domain.model.dto.CustomerProfileDTO;
import com.anax.account.domain.repository.CustomerCacheRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerClientTest {

    @Test
    void whenCustomerServiceFails_thenReturnCachedName() {
        CustomerCacheRepository cacheRepository = mock(CustomerCacheRepository.class);
        when(cacheRepository.findById(7L)).thenReturn(Mono.just(new CachedCustomer(
                7L, "Ada", "Femenino", 31, "ID-7", "Calle 1", "555-0107", true)));
        WebClient.Builder webClientBuilder = WebClient.builder()
                .exchangeFunction(request -> Mono.error(new IllegalStateException("service unavailable")));
        CustomerClient customerClient = new CustomerClient(
                webClientBuilder,
                cacheRepository,
                CircuitBreaker.ofDefaults("customer-service-test"),
                Retry.of("customer-service-test", RetryConfig.custom().maxAttempts(1).build()),
                "http://customer-service/api/v1/customers",
                Duration.ofMillis(100));

        StepVerifier.create(customerClient.getCustomerName(7L))
                .expectNext("Ada")
                .verifyComplete();

        verify(cacheRepository).findById(7L);
    }

    @Test
    void whenCustomerServiceResponds_thenUpdateOnlyCachedName() {
        CustomerCacheRepository cacheRepository = mock(CustomerCacheRepository.class);
        when(cacheRepository.upsertName(7L, "Ada")).thenReturn(Mono.just(1));
        when(cacheRepository.findById(7L)).thenReturn(Mono.empty());
        WebClient.Builder webClientBuilder = WebClient.builder()
                .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("{\"name\":\"Ada\"}")
                        .build()));
        CustomerClient customerClient = new CustomerClient(
                webClientBuilder,
                cacheRepository,
                CircuitBreaker.ofDefaults("customer-service-name-test"),
                Retry.of("customer-service-name-test", RetryConfig.custom().maxAttempts(1).build()),
                "http://customer-service/api/v1/customers",
                Duration.ofSeconds(2));

        StepVerifier.create(customerClient.getCustomerName(7L))
                .expectNext("Ada")
                .verifyComplete();

        verify(cacheRepository).upsertName(7L, "Ada");
    }

    @Test
    void whenCustomerServiceFails_thenReturnCachedProfilesWithoutPassword() {
        CustomerCacheRepository cacheRepository = mock(CustomerCacheRepository.class);
        when(cacheRepository.findAll()).thenReturn(Flux.just(new CachedCustomer(
                7L, "Ada", "Femenino", 31, "ID-7", "Calle 1", "555-0107", true)));
        WebClient.Builder webClientBuilder = WebClient.builder()
                .exchangeFunction(request -> Mono.error(new IllegalStateException("service unavailable")));
        CustomerClient customerClient = new CustomerClient(
                webClientBuilder,
                cacheRepository,
                CircuitBreaker.ofDefaults("customer-service-all-test"),
                Retry.of("customer-service-all-test", RetryConfig.custom().maxAttempts(1).build()),
                "http://customer-service/api/v1/customers",
                Duration.ofMillis(100));

        StepVerifier.create(customerClient.getAllCustomers())
                .expectNextMatches(customers -> customers.size() == 1
                        && customers.get(0).getId().equals(7L)
                        && customers.get(0).getName().equals("Ada"))
                .verifyComplete();

        verify(cacheRepository).findAll();
    }
}