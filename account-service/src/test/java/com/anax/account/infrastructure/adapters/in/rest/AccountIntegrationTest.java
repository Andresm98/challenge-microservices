package com.anax.account.infrastructure.adapters.in.rest;

import com.anax.account.domain.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

// descartar
@Tag("integration")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"account.security.username=test-user", "account.security.password=test-password"})
public class AccountIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
            .defaultHeaders(headers -> headers.setBasicAuth("test-user", "test-password"))
                .build();
    }

    @Test
    void createAccountIntegrationTest() {

        // =========================
        // ARRANGE: Preparar los datos
        // =========================

        String accountNumber = "test-" + UUID.randomUUID();
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType("Ahorro");
        account.setInitialBalance(500.0);
        account.setStatus(true);
        account.setCustomerId(1L);

        // =========================
        // ACT: Ejecutar la acción a probar
        // =========================

        webTestClient.post()
                .uri("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()

        // =========================
        // ASSERT: Verificar el resultado esperado
        // =========================

                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(accountNumber);
    }

    @Test
    void accountEndpointsRequireAuthentication() {
        WebTestClient anonymousClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        anonymousClient.get()
                .uri("/api/v1/accounts")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createMovementWithoutIdempotencyKeyReturnsBadRequest() {
        webTestClient.post()
                .uri("/api/v1/movements")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"accountId\":1,\"movementType\":\"Deposito\",\"value\":10}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("400")
                .jsonPath("$.message").value(message ->
                        org.assertj.core.api.Assertions.assertThat(message.toString())
                                .contains("Idempotency-Key"));
    }
}
