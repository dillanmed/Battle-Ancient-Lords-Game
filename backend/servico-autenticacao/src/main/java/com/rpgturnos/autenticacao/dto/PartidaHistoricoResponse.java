package com.rpgturnos.autenticacao.dto;

import java.time.Instant;

public record PartidaHistoricoResponse(
        Long id,
        Long batalhaId,
        Long personagemId,
        String inimigoNome,
        String resultado,
        Integer vidaFinalPersonagem,
        Instant finalizadaEm
) {
}
