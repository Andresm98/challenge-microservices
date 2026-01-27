package com.anax.account.infrastructure.adapters.out.messaging;

import com.anax.account.domain.model.Movement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "movement-events";

    public void sendMovementEvent(Movement movement) {
        log.info("Enviando evento de movimiento a Kafka: {}", movement.getId());
        kafkaTemplate.send(TOPIC, movement);
    }
}