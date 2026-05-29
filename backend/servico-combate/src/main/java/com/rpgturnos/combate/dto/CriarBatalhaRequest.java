package com.rpgturnos.combate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarBatalhaRequest {

    private Long usuarioId;
    private Long personagemId;

    private String nomeJogador;
    private Integer vidaJogador;
    private Integer manaJogador;
    private Integer ataqueJogador;
    private Integer defesaJogador;
    private Integer nivelJogador;
}