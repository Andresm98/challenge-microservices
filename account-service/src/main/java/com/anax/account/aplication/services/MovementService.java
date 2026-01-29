package com.anax.account.aplication.services;

import com.anax.account.domain.exception.InsufficientBalanceException;
import com.anax.account.domain.model.Movement;
import com.anax.account.domain.repository.AccountRepository;
import com.anax.account.domain.repository.MovementRepository;
import com.anax.account.infrastructure.adapters.out.messaging.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementService {
    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final KafkaProducer kafkaProducer; // Inyectar el productor

    @Transactional
    public Mono<Movement> createMovement(Movement movement) {
        return accountRepository.findById(movement.getAccountId())
                .switchIfEmpty(Mono.error(new RuntimeException("Cuenta no existe")))
                .flatMap(account -> {
                    // Lógica F2: Normalizar el valor según el tipo de movimiento
                    double valueToApply = movement.getValue();

                    // Si es Retiro/Débito y el valor viene positivo se convierte a negativo
                    if (movement.getMovementType().equalsIgnoreCase("Retiro") && valueToApply > 0) {
                        valueToApply = -valueToApply;
                    }

                    // Si es Deposito/Crédito y viene negativo (error de usuario) se convierte a positivo
                    else if (movement.getMovementType().equalsIgnoreCase("Deposito") && valueToApply < 0) {
                        valueToApply = Math.abs(valueToApply);
                    }

                    // Requisito Atómico: Validación si la cuenta está activa o no
                    boolean status = account.getStatus();
                    if (!status) {
                        log.info("La cuenta con ID {} está inactiva. No se puede procesar el movimiento.", account.getId());
                        return Mono.error(new RuntimeException("La cuenta está inactiva"));
                    }

                    // Requisito F3: Validación del saldo

                    double newBalance = account.getInitialBalance() + valueToApply;

                    if (newBalance < 0) {
                        return Mono.error(new InsufficientBalanceException("Saldo no disponible"));
                    }

                    // Setear los valores finales antes de guardar
                    movement.setValue(valueToApply); // Valores reales (ejem: -1000)
                    movement.setBalance(newBalance);
                    movement.setDate(LocalDateTime.now());

                    account.setInitialBalance(newBalance);

                    return accountRepository.save(account)
                            .then(movementRepository.save(movement))
                            .doOnSuccess(kafkaProducer::sendMovementEvent);
                });
    }
}