package com.anax.account.aplication.services;

import com.anax.account.domain.model.MovementRecordedEvent;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovementNotificationService {

    // Convertir a String para enviar el texto plano de la notificación
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String NOTIFICATION_TOPIC = "notificacion-movements";

    @KafkaListener(topics = "movement-events", groupId = "account-movement-notifications")
    public Mono<Void> consumeMovementEvent(String payload) {
        return Mono.fromCallable(() -> objectMapper.readValue(payload, MovementRecordedEvent.class))
                .flatMap(event -> {
                    String message = String.format(
                            "NOTIFICACIÓN: Se ha realizado un %s por valor de %.2f. Saldo disponible: %.2f",
                            event.getMovementType(), event.getValue(), event.getBalance());
                    return Mono.fromFuture(kafkaTemplate.send(
                            NOTIFICATION_TOPIC, event.getEventId(), message)).then();
                })
                .doOnSuccess(ignored -> log.info("Notificación generada desde movement-events"));
    }
}