package com.rpgturnos.combate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inimigo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Integer fase;

    private Integer nivel;

    private Integer vidaMaxima;

    private Integer ataque;

    private Integer defesa;

    private Boolean boss;

    private Integer recompensaXp;

    private Boolean ativo;

    private String spriteKey;

    private String tipo;

    private String descricao;
}
