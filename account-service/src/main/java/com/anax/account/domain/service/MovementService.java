package com.anax.account.domain.service;

import com.anax.account.domain.exception.InsufficientBalanceException;
import com.anax.account.domain.model.Movement;
import com.anax.account.domain.repository.AccountRepository;
import com.anax.account.domain.repository.MovementRepository;
import com.anax.account.infrastructure.adapters.out.messaging.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovementService {
    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final KafkaProducer kafkaProducer; // Inyectamos el productor

    @Transactional
    public Mono<Movement> createMovement(Movement movement) {
        return accountRepository.findById(movement.getAccountId())
                .switchIfEmpty(Mono.error(new RuntimeException("Cuenta no existe")))
                .flatMap(account -> {
                    double newBalance = account.getInitialBalance() + movement.getValue();

                    if (newBalance < 0) {
                        return Mono.error(new InsufficientBalanceException("Saldo no disponible"));
                    }

                    account.setInitialBalance(newBalance);
                    movement.setBalance(newBalance);
                    movement.setDate(LocalDateTime.now());

                    return accountRepository.save(account)
                            .then(movementRepository.save(movement))
                            .doOnSuccess(kafkaProducer::sendMovementEvent); // Enviamos el evento al éxito
                });
    }
}