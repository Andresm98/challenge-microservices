package com.anax.account.aplication.services;

import com.anax.account.domain.model.dto.AccountStatementDTO;
import com.anax.account.domain.repository.AccountRepository;
import com.anax.account.domain.repository.MovementRepository;
import com.anax.account.infrastructure.adapters.out.external.CustomerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;

    public Flux<AccountStatementDTO> generateReport(Long clientId, LocalDateTime start, LocalDateTime end) {
        // 1. Obtener cliente del otro microservicio
        return customerClient.getCustomerName(clientId)
                .flatMapMany(customerName ->
                        // 2. Buscar los movimientos en el rango de fechas (start, end)
                        movementRepository.findAllByClientIdAndDateRange(clientId, start, end)
                                .flatMap(movement ->
                                        // 3. Por cada movimiento, buscar su cuenta para completar el DTO
                                        accountRepository.findById(movement.getAccountId())
                                                .map(account -> AccountStatementDTO.builder()
                                                        .date(movement.getDate())
                                                        .customer(customerName)
                                                        .accountNumber(account.getAccountNumber())
                                                        .type(account.getAccountType())
                                                        .initialBalance(account.getInitialBalance() - movement.getValue()) // Saldo antes del mov
                                                        .status(account.getStatus())
                                                        .movement(movement.getValue())
                                                        .availableBalance(movement.getBalance())
                                                        .build())
                                )
                );
    }
}