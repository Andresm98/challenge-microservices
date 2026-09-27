package com.anax.account.infrastructure.adapters.in.rest;

import com.anax.account.aplication.services.MovementService;
import com.anax.account.domain.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.mock;

class MovementControllerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MovementController controller = new MovementController(
                mock(MovementService.class), mock(MovementRepository.class));
        webTestClient = WebTestClient.bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingIdempotencyKeyReturnsBadRequest() {
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