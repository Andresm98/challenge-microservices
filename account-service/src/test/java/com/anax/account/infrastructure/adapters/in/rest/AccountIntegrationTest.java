package com.anax.account.infrastructure.adapters.in.rest;

import com.anax.account.domain.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void createAccountIntegrationTest() {

        // =========================
        // ARRANGE: Preparar los datos
        // =========================

        Account account = new Account();
        account.setAccountNumber("999999");
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
                .jsonPath("$.accountNumber").isEqualTo("999999");
    }
}
