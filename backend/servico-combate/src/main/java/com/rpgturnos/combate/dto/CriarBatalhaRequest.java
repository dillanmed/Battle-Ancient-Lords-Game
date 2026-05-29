package com.rpgturnos.combate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarBatalhaRequest {

    private Long usuarioId;
    private Long personagemId;
}
