package com.rpgturnos.autenticacao.dto;

public record AuthResponse(
        Long usuarioId,
        String nome,
        String email,
        String tipoToken,
        String token
) {
}
