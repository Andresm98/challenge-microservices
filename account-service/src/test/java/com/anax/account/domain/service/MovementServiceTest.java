package com.anax.account.domain.service;

import com.anax.account.domain.exception.InsufficientBalanceException;
import com.anax.account.domain.model.Account;
import com.anax.account.domain.model.Movement;
import com.anax.account.domain.repository.AccountRepository;
import com.anax.account.domain.repository.MovementRepository;
import com.anax.account.infrastructure.adapters.out.messaging.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// JUnit 5 + Mockito + Reactor Test (reactive streams (MONO y FLUX))

@ExtendWith(MockitoExtension.class)
public class MovementServiceTest {

    @Mock private MovementRepository movementRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private KafkaProducer kafkaProducer;

    @InjectMocks private MovementService movementService;

    private Account mockAccount;

    @BeforeEach
    void setUp() {
        // Arrange: Inicializar los datos comunes de prueba
        mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setInitialBalance(100.0);
        mockAccount.setStatus(true); // importante para evitar NullPointerException
    }

    @Test
    void whenWithdrawalExceedsBalance_thenThrowException() {
        // Arrange: Crear el movimiento que excede el saldo
        Movement movement = new Movement();
        movement.setAccountId(1L);
        movement.setMovementType("Retiro");
        movement.setValue(200.0); // Intenta retirar 200 teniendo 100

        when(accountRepository.findById(1L)).thenReturn(Mono.just(mockAccount));

        // Act & Assert: Ejecutar el servicio y verificar que lanza la excepción
        StepVerifier.create(movementService.createMovement(movement))
                .expectError(InsufficientBalanceException.class)
                .verify();
    }

    @Test
    void whenDeposit_thenSuccess() {
        // Arrange: Crear el movimiento de depósito
        Movement movement = new Movement();
        movement.setAccountId(1L);
        movement.setMovementType("Deposito");
        movement.setValue(50.0);

        // Configurar mocks
        when(accountRepository.findById(1L)).thenReturn(Mono.just(mockAccount));
        when(accountRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(movementRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert: Ejecutar el servicio y verificar que el balance se actualizó correctamente
        StepVerifier.create(movementService.createMovement(movement))
                .expectNextMatches(m -> m.getBalance() == 150.0) // Assert
                .verifyComplete();
    }
}
