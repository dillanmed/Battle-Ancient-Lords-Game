package com.rpgturnos.autenticacao.excecao;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        Map<String, String> campos
) {
}
