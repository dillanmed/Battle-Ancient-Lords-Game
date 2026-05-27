package com.rpgturnos.combate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "batalhas")
public class Batalha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long personagemId;

    private Long usuarioId;

    private String inimigoNome;

    private Integer vidaPersonagem;

    private Integer vidaInimigo;

    private Integer turnoAtual;

    private String status;

    private String resultado;

    public Batalha() {
    }

    public Batalha(Long id, Long personagemId, Long usuarioId, String inimigoNome, Integer vidaPersonagem,
                   Integer vidaInimigo, Integer turnoAtual, String status, String resultado) {
        this.id = id;
        this.personagemId = personagemId;
        this.usuarioId = usuarioId;
        this.inimigoNome = inimigoNome;
        this.vidaPersonagem = vidaPersonagem;
        this.vidaInimigo = vidaInimigo;
        this.turnoAtual = turnoAtual;
        this.status = status;
        this.resultado = resultado;
    }

    public static BatalhaBuilder builder() {
        return new BatalhaBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonagemId() {
        return personagemId;
    }

    public void setPersonagemId(Long personagemId) {
        this.personagemId = personagemId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getInimigoNome() {
        return inimigoNome;
    }

    public void setInimigoNome(String inimigoNome) {
        this.inimigoNome = inimigoNome;
    }

    public Integer getVidaPersonagem() {
        return vidaPersonagem;
    }

    public void setVidaPersonagem(Integer vidaPersonagem) {
        this.vidaPersonagem = vidaPersonagem;
    }

    public Integer getVidaInimigo() {
        return vidaInimigo;
    }

    public void setVidaInimigo(Integer vidaInimigo) {
        this.vidaInimigo = vidaInimigo;
    }

    public Integer getTurnoAtual() {
        return turnoAtual;
    }

    public void setTurnoAtual(Integer turnoAtual) {
        this.turnoAtual = turnoAtual;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public static class BatalhaBuilder {

        private Long id;
        private Long personagemId;
        private Long usuarioId;
        private String inimigoNome;
        private Integer vidaPersonagem;
        private Integer vidaInimigo;
        private Integer turnoAtual;
        private String status;
        private String resultado;

        public BatalhaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BatalhaBuilder personagemId(Long personagemId) {
            this.personagemId = personagemId;
            return this;
        }

        public BatalhaBuilder usuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public BatalhaBuilder inimigoNome(String inimigoNome) {
            this.inimigoNome = inimigoNome;
            return this;
        }

        public BatalhaBuilder vidaPersonagem(Integer vidaPersonagem) {
            this.vidaPersonagem = vidaPersonagem;
            return this;
        }

        public BatalhaBuilder vidaInimigo(Integer vidaInimigo) {
            this.vidaInimigo = vidaInimigo;
            return this;
        }

        public BatalhaBuilder turnoAtual(Integer turnoAtual) {
            this.turnoAtual = turnoAtual;
            return this;
        }

        public BatalhaBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BatalhaBuilder resultado(String resultado) {
            this.resultado = resultado;
            return this;
        }

        public Batalha build() {
            return new Batalha(id, personagemId, usuarioId, inimigoNome, vidaPersonagem, vidaInimigo, turnoAtual, status, resultado);
        }
    }
}
