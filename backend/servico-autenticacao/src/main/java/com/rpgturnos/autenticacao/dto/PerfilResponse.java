package com.rpgturnos.autenticacao.dto;

import java.time.Instant;
import java.util.List;

public record PerfilResponse(
        Long usuarioId,
        String nome,
        String email,
        Integer nivelConta,
        Integer totalPartidas,
        Integer vitorias,
        Integer derrotas,
        Instant historicoAtualizadoEm,
        List<PartidaHistoricoResponse> historicoPartidas
) {
}
