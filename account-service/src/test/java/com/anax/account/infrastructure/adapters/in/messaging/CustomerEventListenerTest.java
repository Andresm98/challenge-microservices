package com.anax.account.infrastructure.adapters.in.messaging;

import com.anax.account.domain.repository.CustomerCacheRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerEventListenerTest {

    @Test
    void consumeUpsertsCustomerProjectionWithoutPassword() {
        CustomerCacheRepository repository = mock(CustomerCacheRepository.class);
        when(repository.upsert(7L, "Ada", "Femenino", 31, "ID-7", "Calle 1", "555-0107", true))
                .thenReturn(Mono.just(1));
        CustomerEventListener listener = new CustomerEventListener(new ObjectMapper(), repository);
        String payload = "{\"eventId\":\"customer-7-v1\",\"eventType\":\"UPDATED\",\"customerId\":7," +
                "\"name\":\"Ada\",\"gender\":\"Femenino\",\"age\":31,\"identification\":\"ID-7\"," +
                "\"address\":\"Calle 1\",\"phone\":\"555-0107\",\"status\":true," +
                "\"occurredAt\":\"2026-09-26T12:00:00Z\"}";

        StepVerifier.create(listener.consume(payload)).verifyComplete();

        verify(repository).upsert(7L, "Ada", "Femenino", 31, "ID-7", "Calle 1", "555-0107", true);
    }
}