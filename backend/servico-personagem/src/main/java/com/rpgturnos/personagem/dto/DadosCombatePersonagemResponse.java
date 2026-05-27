package com.rpgturnos.personagem.dto;

import com.rpgturnos.personagem.model.ClassePersonagem;
import com.rpgturnos.personagem.model.Personagem;

import java.util.List;

public class DadosCombatePersonagemResponse {

    private Long id;
    private String nome;
    private ClassePersonagem classe;
    private Integer nivel;
    private Integer vidaMaxima;
    private Integer manaMaxima;
    private Integer ataque;
    private Integer defesa;
    private Integer forca;
    private Integer inteligencia;
    private Integer agilidade;
    private List<HabilidadeResponse> habilidades;

    public DadosCombatePersonagemResponse() {
    }

    public DadosCombatePersonagemResponse(Long id, String nome, ClassePersonagem classe, Integer nivel,
                                          Integer vidaMaxima, Integer manaMaxima, Integer ataque, Integer defesa,
                                          Integer forca, Integer inteligencia, Integer agilidade,
                                          List<HabilidadeResponse> habilidades) {
        this.id = id;
        this.nome = nome;
        this.classe = classe;
        this.nivel = nivel;
        this.vidaMaxima = vidaMaxima;
        this.manaMaxima = manaMaxima;
        this.ataque = ataque;
        this.defesa = defesa;
        this.forca = forca;
        this.inteligencia = inteligencia;
        this.agilidade = agilidade;
        this.habilidades = habilidades;
    }

    public static DadosCombatePersonagemResponse from(Personagem personagem, List<HabilidadeResponse> habilidades) {
        return new DadosCombatePersonagemResponse(personagem.getId(), personagem.getNome(), personagem.getClasse(),
                personagem.getNivel(), personagem.getVidaMaxima(), personagem.getManaMaxima(),
                personagem.getAtaque(), personagem.getDefesa(), personagem.getForca(),
                personagem.getInteligencia(), personagem.getAgilidade(), habilidades);
    }

    public Long getId() {
        return id;
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

    public List<HabilidadeResponse> getHabilidades() {
        return habilidades;
    }
}
