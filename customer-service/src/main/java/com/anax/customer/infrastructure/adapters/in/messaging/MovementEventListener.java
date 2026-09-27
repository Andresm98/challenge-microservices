package com.anax.customer.infrastructure.adapters.in.messaging;

import com.anax.customer.domain.model.CustomerMovementEvent;
import com.anax.customer.domain.repository.CustomerMovementActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovementEventListener {

    private final ObjectMapper objectMapper;
    private final CustomerMovementActivityRepository activityRepository;

    @KafkaListener(topics = "movement-events", groupId = "customer-service-movements")
    public Mono<Void> consume(String payload) {
        return Mono.fromCallable(() -> objectMapper.readValue(payload, CustomerMovementEvent.class))
                .flatMap(event -> activityRepository.record(
                                event.getEventId(),
                                event.getMovementId(),
                                event.getCustomerId(),
                                event.getAccountId(),
                                event.getMovementType(),
                                event.getValue(),
                                event.getBalance(),
                                LocalDateTime.parse(event.getOccurredAt()))
                        .doOnSuccess(ignored -> log.info("Movimiento {} recibido para cliente {}",
                                event.getMovementId(), event.getCustomerId())))
                .then();
    }
}