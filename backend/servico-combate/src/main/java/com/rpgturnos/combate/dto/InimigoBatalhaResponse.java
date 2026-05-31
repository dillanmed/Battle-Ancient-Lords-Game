package com.rpgturnos.combate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InimigoBatalhaResponse {

    private Long id;
    private String nome;
    private Integer vidaAtual;
    private Integer vidaMaxima;
    private Boolean vivo;
}
