package com.rpgturnos.combate.client;

import java.util.List;

public class DadosCombatePersonagemResponse {

    private Long id;
    private String nome;
    private String classe;
    private Integer nivel;
    private Integer vidaMaxima;
    private Integer manaMaxima;
    private Integer ataque;
    private Integer defesa;
    private Integer forca;
    private Integer inteligencia;
    private Integer agilidade;
    private List<HabilidadePersonagemResponse> habilidades;

    public DadosCombatePersonagemResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getClasse() {
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

    public List<HabilidadePersonagemResponse> getHabilidades() {
        return habilidades;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public void setVidaMaxima(Integer vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
    }

    public void setManaMaxima(Integer manaMaxima) {
        this.manaMaxima = manaMaxima;
    }

    public void setAtaque(Integer ataque) {
        this.ataque = ataque;
    }

    public void setDefesa(Integer defesa) {
        this.defesa = defesa;
    }

    public void setForca(Integer forca) {
        this.forca = forca;
    }

    public void setInteligencia(Integer inteligencia) {
        this.inteligencia = inteligencia;
    }

    public void setAgilidade(Integer agilidade) {
        this.agilidade = agilidade;
    }

    public void setHabilidades(List<HabilidadePersonagemResponse> habilidades) {
        this.habilidades = habilidades;
    }
}
