package com.anax.customer.infrastructure.adapters.in.messaging;

import com.anax.customer.domain.repository.CustomerMovementActivityRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovementEventListenerTest {

    @Test
    void consumeRecordsMovementIdempotentlyByEventId() {
        CustomerMovementActivityRepository repository = mock(CustomerMovementActivityRepository.class);
        when(repository.record("movement-99", 99L, 7L, 12L, "Deposito", 25.0, 125.0,
                java.time.LocalDateTime.parse("2026-09-26T12:00:00"))).thenReturn(Mono.just(1));
        MovementEventListener listener = new MovementEventListener(new ObjectMapper(), repository);
        String payload = "{\"eventId\":\"movement-99\",\"movementId\":99,\"customerId\":7," +
                "\"accountId\":12,\"movementType\":\"Deposito\",\"value\":25.0,\"balance\":125.0," +
                "\"occurredAt\":\"2026-09-26T12:00:00\"}";

        StepVerifier.create(listener.consume(payload)).verifyComplete();

        verify(repository).record("movement-99", 99L, 7L, 12L, "Deposito", 25.0, 125.0,
                java.time.LocalDateTime.parse("2026-09-26T12:00:00"));
    }
}