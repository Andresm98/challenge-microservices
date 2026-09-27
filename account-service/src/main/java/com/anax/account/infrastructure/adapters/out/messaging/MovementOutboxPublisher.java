package com.anax.account.infrastructure.adapters.out.messaging;

import com.anax.account.domain.repository.MovementOutboxRepository;
import com.anax.account.domain.model.MovementRecordedEvent;
import com.anax.account.domain.repository.AccountRepository;
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
    private final AccountRepository accountRepository;
    private final KafkaProducer kafkaProducer;

    @Scheduled(fixedDelayString = "${outbox.poll-interval:1000}")
    public Mono<Void> publishPending() {
        return outboxRepository.findPending()
                .concatMap(outboxEvent -> movementRepository.findById(outboxEvent.getMovementId())
                        .switchIfEmpty(Mono.error(new IllegalStateException("Movimiento del outbox no encontrado")))
                    .flatMap(movement -> accountRepository.findById(movement.getAccountId())
                        .switchIfEmpty(Mono.error(new IllegalStateException("Cuenta del movimiento no encontrada")))
                        .flatMap(account -> kafkaProducer.sendMovementEventReactive(new MovementRecordedEvent(
                            "movement-" + movement.getId(),
                            movement.getId(),
                            account.getCustomerId(),
                            account.getId(),
                            movement.getMovementType(),
                            movement.getValue(),
                            movement.getBalance(),
                            movement.getDate().toString()))))
                        .then(Mono.defer(() -> {
                        outboxEvent.setPublished(true);
                        return outboxRepository.save(outboxEvent).then();
                        }))
                        .onErrorResume(error -> {
                        log.error("No se pudo publicar el evento outbox {}", outboxEvent.getId(), error);
                            return Mono.empty();
                        }))
                .then();
    }
}