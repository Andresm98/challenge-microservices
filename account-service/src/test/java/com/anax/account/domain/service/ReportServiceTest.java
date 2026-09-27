package com.anax.account.domain.service;

import com.anax.account.aplication.services.ReportService;
import com.anax.account.domain.model.Account;
import com.anax.account.domain.model.Movement;
import com.anax.account.domain.model.dto.CustomerProfileDTO;
import com.anax.account.domain.model.dto.UserReportDTO;
import com.anax.account.domain.repository.AccountRepository;
import com.anax.account.domain.repository.MovementRepository;
import com.anax.account.infrastructure.adapters.out.external.CustomerClient;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    @Test
    void completeReportNestsAccountsAndAllMovementsUnderEachUser() {
        MovementRepository movementRepository = mock(MovementRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        CustomerClient customerClient = mock(CustomerClient.class);
        ReportService reportService = new ReportService(movementRepository, accountRepository, customerClient);

        CustomerProfileDTO customer = new CustomerProfileDTO(
                7L, "Ada", "Femenino", 31, "ID-7", "Calle 1", "555-0107", true);
        Account account = new Account();
        account.setId(12L);
        account.setAccountNumber("ACC-12");
        account.setAccountType("Ahorros");
        account.setInitialBalance(100.0);
        account.setStatus(true);
        Movement movement = new Movement();
        movement.setId(99L);
        movement.setAccountId(12L);
        movement.setDate(LocalDateTime.parse("2026-09-26T12:00:00"));
        movement.setMovementType("Deposito");
        movement.setValue(25.0);
        movement.setBalance(125.0);

        when(customerClient.getAllCustomers()).thenReturn(Mono.just(List.of(customer)));
        when(accountRepository.findAllByCustomerId(7L)).thenReturn(Flux.just(account));
        when(movementRepository.findAllByAccountId(12L)).thenReturn(Flux.just(movement));

        StepVerifier.create(reportService.generateCompleteReport())
                .expectNextMatches(report -> report.getUsuario().getId().equals(7L)
                        && report.getUsuario().getName().equals("Ada")
                        && report.getCuentas().size() == 1
                        && report.getCuentas().get(0).getId().equals(12L)
                        && report.getCuentas().get(0).getMovimientos().size() == 1
                        && report.getCuentas().get(0).getMovimientos().get(0).getId().equals(99L))
                .verifyComplete();
    }
}