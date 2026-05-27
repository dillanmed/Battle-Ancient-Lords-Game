package com.rpgturnos.autenticacao.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
        name = "historico_partidas",
        uniqueConstraints = @UniqueConstraint(name = "uk_historico_partidas_batalha", columnNames = "batalha_id")
)
public class HistoricoPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(name = "batalha_id", nullable = false)
    private Long batalhaId;

    private Long personagemId;

    @Column(nullable = false, length = 120)
    private String inimigoNome;

    @Column(nullable = false, length = 40)
    private String resultado;

    private Integer vidaFinalPersonagem;

    @Column(nullable = false, updatable = false)
    private Instant finalizadaEm;

    public HistoricoPartida() {
    }

    public HistoricoPartida(Long usuarioId, Long batalhaId, Long personagemId, String inimigoNome,
                            String resultado, Integer vidaFinalPersonagem) {
        this.usuarioId = usuarioId;
        this.batalhaId = batalhaId;
        this.personagemId = personagemId;
        this.inimigoNome = inimigoNome;
        this.resultado = resultado;
        this.vidaFinalPersonagem = vidaFinalPersonagem;
    }

    @PrePersist
    void prePersist() {
        finalizadaEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getBatalhaId() {
        return batalhaId;
    }

    public Long getPersonagemId() {
        return personagemId;
    }

    public String getInimigoNome() {
        return inimigoNome;
    }

    public String getResultado() {
        return resultado;
    }

    public Integer getVidaFinalPersonagem() {
        return vidaFinalPersonagem;
    }

    public Instant getFinalizadaEm() {
        return finalizadaEm;
    }
}
