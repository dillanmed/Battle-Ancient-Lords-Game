package com.rpgturnos.combate.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatenteSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long referenciaOriginalId;

    private String nome;

    private String tipo;

    private Integer vidaMaxima;

    private Integer vidaAtual;

    private Integer manaMaxima;

    private Integer manaAtual;

    private Integer ataque;

    private Integer defesa;

    private Integer nivel;
}