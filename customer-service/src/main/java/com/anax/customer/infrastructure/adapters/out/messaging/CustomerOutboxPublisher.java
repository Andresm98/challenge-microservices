package com.anax.customer.infrastructure.adapters.out.messaging;

import com.anax.customer.domain.repository.CustomerOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerOutboxPublisher {

    private static final String TOPIC = "customer-events";

    private final CustomerOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.poll-interval:1000}")
    public Mono<Void> publishPending() {
        return outboxRepository.findPending()
                .concatMap(event -> Mono.fromFuture(kafkaTemplate.send(TOPIC, event.getCustomerId().toString(), event.getPayload()))
                        .then(Mono.defer(() -> {
                            event.setPublished(true);
                            return outboxRepository.save(event).then();
                        }))
                        .onErrorResume(error -> {
                            log.error("No se pudo publicar el evento de cliente {}", event.getEventId(), error);
                            return Mono.empty();
                        }))
                .then();
    }
}