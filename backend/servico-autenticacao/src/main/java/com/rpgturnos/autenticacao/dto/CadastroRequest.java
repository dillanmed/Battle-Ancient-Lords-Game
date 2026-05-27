package com.rpgturnos.autenticacao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(min = 2, max = 80, message = "Nome deve ter entre 2 e 80 caracteres")
        String nome,

        @NotBlank(message = "Email e obrigatorio")
        @Email(message = "Email invalido")
        @Size(max = 120, message = "Email deve ter no maximo 120 caracteres")
        String email,

        @NotBlank(message = "Senha e obrigatoria")
        @Size(min = 6, max = 80, message = "Senha deve ter entre 6 e 80 caracteres")
        String senha
) {
}
