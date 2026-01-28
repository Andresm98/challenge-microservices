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

                    double newBalance = account.getInitialBalance() + valueToApply;

                    // Requisito F3: Validación del saldo
                    if (newBalance < 0) {
                        return Mono.error(new InsufficientBalanceException("Saldo no disponible"));
                    }

                    // Setear los valores finales antes de guardar
                    movement.setValue(valueToApply); // Valore real (ejem: -1000)
                    movement.setBalance(newBalance);
                    movement.setDate(LocalDateTime.now());

                    account.setInitialBalance(newBalance);

                    return accountRepository.save(account)
                            .then(movementRepository.save(movement))
                            .doOnSuccess(kafkaProducer::sendMovementEvent);
                });
    }
}