package com.anax.account.infrastructure.adapters.in.rest;

import com.anax.account.domain.model.Movement;
import com.anax.account.domain.service.MovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Movement> create(@RequestBody Movement movement) {
        return movementService.createMovement(movement);
    }
}