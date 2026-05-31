package com.rpgturnos.combate.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private Integer fase;

    @OneToOne(cascade = CascadeType.ALL)
    private CombatenteSnapshot jogador;

    @OneToOne(cascade = CascadeType.ALL)
    private CombatenteSnapshot inimigo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "batalha_inimigos",
            joinColumns = @JoinColumn(name = "batalha_id"),
            inverseJoinColumns = @JoinColumn(name = "combatente_id")
    )
    @Builder.Default
    private List<CombatenteSnapshot> inimigos = new ArrayList<>();

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
