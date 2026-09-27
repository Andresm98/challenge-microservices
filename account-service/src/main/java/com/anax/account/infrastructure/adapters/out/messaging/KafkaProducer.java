package com.anax.account.infrastructure.adapters.out.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.anax.account.domain.model.MovementRecordedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "movement-events";


    public Mono<Void> sendMovementEventReactive(MovementRecordedEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(payload -> Mono.fromFuture(kafkaTemplate.send(
                        TOPIC, event.getAccountId().toString(), payload)))
                .doOnSuccess(result -> log.info("Evento de movimiento publicado: {}", event.getEventId()))
                .then();
    }
}