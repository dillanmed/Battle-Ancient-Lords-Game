package com.rpgturnos.combate.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabilidadeSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long referenciaOriginalId;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoHabilidade tipo;

    private Integer dano;

    private Integer custoMana;

    private Integer cooldown;
}