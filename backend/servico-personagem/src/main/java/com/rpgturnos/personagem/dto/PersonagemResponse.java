package com.rpgturnos.personagem.dto;

import com.rpgturnos.personagem.model.ClassePersonagem;
import com.rpgturnos.personagem.model.Personagem;

public class PersonagemResponse {

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

    public PersonagemResponse() {
    }

    public PersonagemResponse(Long id, Long usuarioId, String nome, ClassePersonagem classe, Integer nivel,
                              Integer experiencia, Integer vidaMaxima, Integer manaMaxima, Integer ataque,
                              Integer defesa, Integer forca, Integer inteligencia, Integer agilidade) {
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

    public static PersonagemResponse from(Personagem personagem) {
        return new PersonagemResponse(personagem.getId(), personagem.getUsuarioId(), personagem.getNome(),
                personagem.getClasse(), personagem.getNivel(), personagem.getExperiencia(),
                personagem.getVidaMaxima(), personagem.getManaMaxima(), personagem.getAtaque(),
                personagem.getDefesa(), personagem.getForca(), personagem.getInteligencia(),
                personagem.getAgilidade());
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public ClassePersonagem getClasse() {
        return classe;
    }

    public Integer getNivel() {
        return nivel;
    }

    public Integer getExperiencia() {
        return experiencia;
    }

    public Integer getVidaMaxima() {
        return vidaMaxima;
    }

    public Integer getManaMaxima() {
        return manaMaxima;
    }

    public Integer getAtaque() {
        return ataque;
    }

    public Integer getDefesa() {
        return defesa;
    }

    public Integer getForca() {
        return forca;
    }

    public Integer getInteligencia() {
        return inteligencia;
    }

    public Integer getAgilidade() {
        return agilidade;
    }
}
