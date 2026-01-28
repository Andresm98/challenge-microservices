package com.anax.account.infrastructure.adapters.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomerClient {
    private final WebClient webClient;

    public CustomerClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081/api/v1/customers").build();
    }

    public Mono<String> getCustomerName(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(String.class) //
                .map(body -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode node = mapper.readTree(body); // parseo directo
                        return node.path("name").asText();
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing JSON", e);
                    }
                })
                .doOnNext(name -> log.info("Cliente obtenido: {}", name))
                .defaultIfEmpty("Cliente Desconocido");
    }
}