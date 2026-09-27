package com.anax.customer.application;

import com.anax.customer.domain.model.Customer;
import com.anax.customer.domain.model.CustomerOutboxEvent;
import com.anax.customer.domain.repository.CustomerOutboxRepository;
import com.anax.customer.domain.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerApplicationServiceTest {

    @Test
    void createPersistsCustomerAndPasswordFreeOutboxEvent() {
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        CustomerOutboxRepository outboxRepository = mock(CustomerOutboxRepository.class);
        CustomerApplicationService service = new CustomerApplicationService(
                customerRepository, outboxRepository, new ObjectMapper());

        Customer customer = new Customer(null, "Ada", "Femenino", 31, "ID-7", "Calle 1", "555-0107", "secret", true);
        Customer saved = new Customer(7L, "Ada", "Femenino", 31, "ID-7", "Calle 1", "555-0107", "secret", true);
        AtomicReference<CustomerOutboxEvent> savedEvent = new AtomicReference<>();
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(saved));
        when(outboxRepository.save(any(CustomerOutboxEvent.class))).thenAnswer(invocation -> {
            CustomerOutboxEvent event = invocation.getArgument(0);
            savedEvent.set(event);
            return Mono.just(event);
        });

        StepVerifier.create(service.create(customer))
                .expectNext(saved)
                .verifyComplete();

        assertThat(savedEvent.get().getEventType()).isEqualTo("CREATED");
        assertThat(savedEvent.get().getCustomerId()).isEqualTo(7L);
        assertThat(savedEvent.get().getPayload()).contains("\"eventType\":\"CREATED\"");
        assertThat(savedEvent.get().getPayload()).doesNotContain("secret", "password");
    }
}