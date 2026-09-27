package com.anax.account.infrastructure.adapters.out.messaging;

import com.anax.account.domain.model.Movement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "movement-events";

    public void sendMovementEvent(Movement movement) {
        // Implementación para enviar el evento a Kafka (LOGS)
        log.info("Enviando evento de movimiento a Kafka: {}", movement.getId());

        kafkaTemplate.send(TOPIC, movement);
    }

    public Mono<Void> sendMovementEventReactive(Movement movement) {
        return Mono.fromFuture(kafkaTemplate.send(TOPIC, movement.getId().toString(), movement))
                .doOnSuccess(result -> log.info("Evento de movimiento publicado: {}", movement.getId()))
                .then();
    }
}