package com.rpgturnos.combate.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batalha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private Long personagemId;

    @OneToOne(cascade = CascadeType.ALL)
    private CombatenteSnapshot jogador;

    @OneToOne(cascade = CascadeType.ALL)
    private CombatenteSnapshot inimigo;

    @Enumerated(EnumType.STRING)
    private StatusBatalha status;

    @Enumerated(EnumType.STRING)
    private Turno turnoAtual;

    @Enumerated(EnumType.STRING)
    private ResultadoBatalha resultado;

    private Integer roundAtual;

    private LocalDateTime criadaEm;

    private LocalDateTime finalizadaEm;
}