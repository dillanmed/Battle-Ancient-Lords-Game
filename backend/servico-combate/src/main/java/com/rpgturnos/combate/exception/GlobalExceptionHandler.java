package com.rpgturnos.combate.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BatalhaNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleBatalhaNaoEncontrada(BatalhaNaoEncontradaException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({
            BatalhaFinalizadaException.class,
            TurnoInvalidoException.class,
            HabilidadeInvalidaException.class,
            IllegalStateException.class
    })
    public ResponseEntity<Map<String, Object>> handleRegraDeNegocio(RuntimeException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Requisicao invalida");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));
    }
}
