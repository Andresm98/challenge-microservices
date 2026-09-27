// infrastructure/adapters/in/rest/GlobalExceptionHandler.java
package com.anax.account.infrastructure.adapters.in.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(ServerWebInputException ex) {
        String message = ex.getReason() == null ? "Solicitud inválida" : ex.getReason();
        return ResponseEntity
                .badRequest()
                .body(Map.of("message", message, "code", "400"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage(), "code", "500"));
    }

    // TODO:: error "Saldo no disponible" para el otro microservicio
}