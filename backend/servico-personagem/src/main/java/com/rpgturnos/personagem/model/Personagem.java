package com.rpgturnos.personagem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "personagens")
public class Personagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private String nome;

    @Enumerated(EnumType.STRING)
    private ClassePersonagem classe;

    private Integer nivel;

    private Integer experiencia;

    private Integer vidaMaxima;

    private Integer manaMaxima;

    private Integer ataque;

    private Integer defesa;

    private Integer forca;

    private Integer inteligencia;

    private Integer agilidade;

    public Personagem() {
    }

    public Personagem(Long id, Long usuarioId, String nome, ClassePersonagem classe, Integer nivel, Integer experiencia,
                      Integer vidaMaxima, Integer manaMaxima, Integer ataque, Integer defesa, Integer forca,
                      Integer inteligencia, Integer agilidade) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.classe = classe;
        this.nivel = nivel;
        this.experiencia = experiencia;
        this.vidaMaxima = vidaMaxima;
        this.manaMaxima = manaMaxima;
        this.ataque = ataque;
        this.defesa = defesa;
        this.forca = forca;
        this.inteligencia = inteligencia;
        this.agilidade = agilidade;
    }

    public static PersonagemBuilder builder() {
        return new PersonagemBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ClassePersonagem getClasse() {
        return classe;
    }

    public void setClasse(ClassePersonagem classe) {
        this.classe = classe;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(Integer experiencia) {
        this.experiencia = experiencia;
    }

    public Integer getVidaMaxima() {
        return vidaMaxima;
    }

    public void setVidaMaxima(Integer vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
    }

    public Integer getManaMaxima() {
        return manaMaxima;
    }

    public void setManaMaxima(Integer manaMaxima) {
        this.manaMaxima = manaMaxima;
    }

    public Integer getAtaque() {
        return ataque;
    }

    public void setAtaque(Integer ataque) {
        this.ataque = ataque;
    }

    public Integer getDefesa() {
        return defesa;
    }

    public void setDefesa(Integer defesa) {
        this.defesa = defesa;
    }

    public Integer getForca() {
        return forca;
    }

    public void setForca(Integer forca) {
        this.forca = forca;
    }

    public Integer getInteligencia() {
        return inteligencia;
    }

    public void setInteligencia(Integer inteligencia) {
        this.inteligencia = inteligencia;
    }

    public Integer getAgilidade() {
        return agilidade;
    }

    public void setAgilidade(Integer agilidade) {
        this.agilidade = agilidade;
    }

    public static class PersonagemBuilder {

        private Long id;
        private Long usuarioId;
        private String nome;
        private ClassePersonagem classe;
        private Integer nivel;
        private Integer experiencia;
        private Integer vidaMaxima;
        private Integer manaMaxima;
        private Integer ataque;
        private Integer defesa;
        private Integer forca;
        private Integer inteligencia;
        private Integer agilidade;

        public PersonagemBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PersonagemBuilder usuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public PersonagemBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public PersonagemBuilder classe(ClassePersonagem classe) {
            this.classe = classe;
            return this;
        }

        public PersonagemBuilder nivel(Integer nivel) {
            this.nivel = nivel;
            return this;
        }

        public PersonagemBuilder experiencia(Integer experiencia) {
            this.experiencia = experiencia;
            return this;
        }

        public PersonagemBuilder vidaMaxima(Integer vidaMaxima) {
            this.vidaMaxima = vidaMaxima;
            return this;
        }

        public PersonagemBuilder manaMaxima(Integer manaMaxima) {
            this.manaMaxima = manaMaxima;
            return this;
        }

        public PersonagemBuilder ataque(Integer ataque) {
            this.ataque = ataque;
            return this;
        }

        public PersonagemBuilder defesa(Integer defesa) {
            this.defesa = defesa;
            return this;
        }

        public PersonagemBuilder forca(Integer forca) {
            this.forca = forca;
            return this;
        }

        public PersonagemBuilder inteligencia(Integer inteligencia) {
            this.inteligencia = inteligencia;
            return this;
        }

        public PersonagemBuilder agilidade(Integer agilidade) {
            this.agilidade = agilidade;
            return this;
        }

        public Personagem build() {
            return new Personagem(id, usuarioId, nome, classe, nivel, experiencia, vidaMaxima, manaMaxima, ataque,
                    defesa, forca, inteligencia, agilidade);
        }
    }
}
