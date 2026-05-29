package com.rpgturnos.combate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HabilidadeRequest {

    private String nome;
    private String tipo;
    private Integer dano;
    private Integer custoMana;
}