package com.anax.account.aplication.services;

import com.anax.account.domain.model.dto.AccountStatementDTO;
import com.anax.account.domain.model.Account;
import com.anax.account.domain.model.Movement;
import com.anax.account.domain.model.dto.UserReportDTO;
import com.anax.account.domain.repository.AccountRepository;
import com.anax.account.domain.repository.MovementRepository;
import com.anax.account.infrastructure.adapters.out.external.CustomerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;

    public Flux<UserReportDTO> generateCompleteReport() {
        return customerClient.getAllCustomers()
                .flatMapMany(Flux::fromIterable)
                .concatMap(customer -> accountRepository.findAllByCustomerId(customer.getId())
                        .concatMap(this::getAccountReport)
                        .collectList()
                        .map(accounts -> new UserReportDTO(customer, accounts)));
    }

    private Mono<UserReportDTO.AccountDTO> getAccountReport(Account account) {
        return movementRepository.findAllByAccountId(account.getId())
                .map(this::toMovementReport)
                .collectList()
                .map(movements -> new UserReportDTO.AccountDTO(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getAccountType(),
                        account.getInitialBalance(),
                        account.getStatus(),
                        movements));
    }

    private UserReportDTO.MovementDTO toMovementReport(Movement movement) {
        return new UserReportDTO.MovementDTO(
                movement.getId(),
                movement.getDate(),
                movement.getMovementType(),
                movement.getValue(),
                movement.getBalance());
    }

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