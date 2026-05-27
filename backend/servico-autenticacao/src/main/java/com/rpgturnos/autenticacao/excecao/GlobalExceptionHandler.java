package com.rpgturnos.autenticacao.excecao;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> campos = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> campos.put(error.getField(), error.getDefaultMessage()));

        return build(HttpStatus.BAD_REQUEST, "Dados invalidos", "Confira os campos enviados.", campos);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ApiError> handleEmailJaCadastrado(EmailJaCadastradoException exception) {
        return build(HttpStatus.CONFLICT, "Email ja cadastrado", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials() {
        return build(HttpStatus.UNAUTHORIZED, "Credenciais invalidas", "Email ou senha incorretos.", Map.of());
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String erro,
            String mensagem,
            Map<String, String> campos
    ) {
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(),
                status.value(),
                erro,
                mensagem,
                campos
        ));
    }
}
