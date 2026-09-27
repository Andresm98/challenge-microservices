package com.anax.account.infrastructure.adapters.out.messaging;

import com.anax.account.domain.repository.MovementOutboxRepository;
import com.anax.account.domain.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovementOutboxPublisher {

    private final MovementOutboxRepository outboxRepository;
    private final MovementRepository movementRepository;
    private final KafkaProducer kafkaProducer;

    @Scheduled(fixedDelayString = "${outbox.poll-interval:1000}")
    public Mono<Void> publishPending() {
        return outboxRepository.findPending()
                .concatMap(event -> movementRepository.findById(event.getMovementId())
                        .switchIfEmpty(Mono.error(new IllegalStateException("Movimiento del outbox no encontrado")))
                        .flatMap(kafkaProducer::sendMovementEventReactive)
                        .then(Mono.defer(() -> {
                            event.setPublished(true);
                            return outboxRepository.save(event).then();
                        }))
                        .onErrorResume(error -> {
                            log.error("No se pudo publicar el evento outbox {}", event.getId(), error);
                            return Mono.empty();
                        }))
                .then();
    }
}