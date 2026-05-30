package com.rpgturnos.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Login ou email e obrigatorio")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        String senha
) {
}
