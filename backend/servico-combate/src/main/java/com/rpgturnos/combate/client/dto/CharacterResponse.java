package com.rpgturnos.combate.client.dto;

import java.util.List;

public class CharacterResponse {

    private Long id;
    private String nome;
    private String classe;
    private Integer vida;
    private Integer mana;
    private Integer ataque;
    private Integer defesa;
    private Integer nivel;
    private List<SkillResponse> habilidades;

    public CharacterResponse() {
    }

    public CharacterResponse(Long id, String nome, String classe, Integer vida, Integer mana,
                             Integer ataque, Integer defesa, Integer nivel,
                             List<SkillResponse> habilidades) {
        this.id = id;
        this.nome = nome;
        this.classe = classe;
        this.vida = vida;
        this.mana = mana;
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = nivel;
        this.habilidades = habilidades;
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

    public Integer getVida() {
        return vida;
    }

    public Integer getMana() {
        return mana;
    }

    public Integer getAtaque() {
        return ataque;
    }

    public Integer getDefesa() {
        return defesa;
    }

    public Integer getNivel() {
        return nivel;
    }

    public List<SkillResponse> getHabilidades() {
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

    public void setVida(Integer vida) {
        this.vida = vida;
    }

    public void setMana(Integer mana) {
        this.mana = mana;
    }

    public void setAtaque(Integer ataque) {
        this.ataque = ataque;
    }

    public void setDefesa(Integer defesa) {
        this.defesa = defesa;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public void setHabilidades(List<SkillResponse> habilidades) {
        this.habilidades = habilidades;
    }
}