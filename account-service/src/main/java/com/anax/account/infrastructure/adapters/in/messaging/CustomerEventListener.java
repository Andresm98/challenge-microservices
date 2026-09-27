package com.anax.account.infrastructure.adapters.in.messaging;

import com.anax.account.domain.model.CustomerChangedEvent;
import com.anax.account.domain.repository.CustomerCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

    private final ObjectMapper objectMapper;
    private final CustomerCacheRepository customerCacheRepository;

    @KafkaListener(topics = "customer-events", groupId = "account-service-customer-projection")
    public Mono<Void> consume(String payload) {
        return Mono.fromCallable(() -> objectMapper.readValue(payload, CustomerChangedEvent.class))
                .flatMap(event -> {
                    if ("DELETED".equals(event.getEventType())) {
                        return customerCacheRepository.deleteById(event.getCustomerId());
                    }
                    if (!"CREATED".equals(event.getEventType()) && !"UPDATED".equals(event.getEventType())) {
                        return Mono.error(new IllegalArgumentException("Tipo de evento customer desconocido"));
                    }
                    return customerCacheRepository.upsert(
                            event.getCustomerId(), event.getName(), event.getGender(), event.getAge(),
                            event.getIdentification(), event.getAddress(), event.getPhone(), event.getStatus())
                            .then();
                })
                .doOnSuccess(ignored -> log.info("Evento de cliente aplicado"))
                .then();
    }
}