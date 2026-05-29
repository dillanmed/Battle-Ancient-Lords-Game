package com.rpgturnos.personagem.exception;

import com.rpgturnos.personagem.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonagemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonagemNotFound(PersonagemNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, "Personagem nao encontrado", exception.getMessage());
    }

    @ExceptionHandler(HabilidadeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHabilidadeNotFound(HabilidadeNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, "Habilidade nao encontrada", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Requisicao invalida", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String mensagem = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Payload invalido");
        return buildResponse(HttpStatus.BAD_REQUEST, "Erro de validacao", mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadablePayload(HttpMessageNotReadableException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Payload invalido",
                "Verifique o JSON enviado e os valores informados.");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String erro, String mensagem) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), erro, mensagem));
    }
}
