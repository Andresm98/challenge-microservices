package com.anax.account.domain.service;

import com.anax.account.aplication.services.MovementService;
import com.anax.account.domain.exception.InsufficientBalanceException;
import com.anax.account.domain.model.Account;
import com.anax.account.domain.model.Movement;
import com.anax.account.domain.repository.AccountRepository;
import com.anax.account.domain.repository.MovementRepository;
import com.anax.account.domain.repository.MovementOutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// JUnit 5 + Mockito + Reactor Test (reactive streams (MONO y FLUX))

@ExtendWith(MockitoExtension.class)
public class MovementServiceTest {

    @Mock private MovementRepository movementRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private MovementOutboxRepository movementOutboxRepository;

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

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Mono.just(mockAccount));
        when(movementRepository.findByIdempotencyKey(anyString())).thenReturn(Mono.empty());

        // Act & Assert: Ejecutar el servicio y verificar que lanza la excepción
        StepVerifier.create(movementService.createMovement(movement, "withdrawal-1"))
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
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Mono.just(mockAccount));
        when(movementRepository.findByIdempotencyKey(anyString())).thenReturn(Mono.empty());
        when(accountRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(movementRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(movementOutboxRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert: Ejecutar el servicio y verificar que el balance se actualizó correctamente
        StepVerifier.create(movementService.createMovement(movement, "deposit-1"))
                .expectNextMatches(m -> m.getBalance() == 150.0) // Assert
                .verifyComplete();

        verify(movementOutboxRepository).save(any());
    }

    @Test
    void whenIdempotencyKeyWasAlreadyProcessed_thenReturnExistingMovementWithoutChangingBalance() {
        Movement existingMovement = new Movement();
        existingMovement.setId(10L);
        existingMovement.setBalance(150.0);
        existingMovement.setIdempotencyKey("deposit-1");
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Mono.just(mockAccount));
        when(movementRepository.findByIdempotencyKey("deposit-1")).thenReturn(Mono.just(existingMovement));

        Movement duplicateRequest = new Movement();
        duplicateRequest.setAccountId(1L);
        StepVerifier.create(movementService.createMovement(duplicateRequest, "deposit-1"))
                .expectNext(existingMovement)
                .verifyComplete();

        verify(accountRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
        verify(movementOutboxRepository, never()).save(any());
    }
}
