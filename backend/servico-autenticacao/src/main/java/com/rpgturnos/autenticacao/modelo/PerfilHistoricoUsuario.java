package com.rpgturnos.autenticacao.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "perfil_historico_usuarios")
public class PerfilHistoricoUsuario {

    @Id
    private Long usuarioId;

    @Column(nullable = false)
    private Integer totalPartidas = 0;

    @Column(nullable = false)
    private Integer vitorias = 0;

    @Column(nullable = false)
    private Integer derrotas = 0;

    @Column(nullable = false)
    private Integer nivelConta = 1;

    @Column(nullable = false)
    private Instant atualizadoEm = Instant.now();

    public PerfilHistoricoUsuario() {
    }

    public PerfilHistoricoUsuario(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void registrar(HistoricoPartida partida) {
        totalPartidas++;

        if ("VITORIA".equalsIgnoreCase(partida.getResultado())) {
            vitorias++;
        } else {
            derrotas++;
        }

        nivelConta = calcularNivel(totalPartidas, vitorias);
        atualizadoEm = Instant.now();
    }

    private int calcularNivel(int totalPartidas, int vitorias) {
        return 1 + (totalPartidas / 5) + (vitorias / 3);
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Integer getTotalPartidas() {
        return totalPartidas;
    }

    public Integer getVitorias() {
        return vitorias;
    }

    public Integer getDerrotas() {
        return derrotas;
    }

    public Integer getNivelConta() {
        return nivelConta;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
