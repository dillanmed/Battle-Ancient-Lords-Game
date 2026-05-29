package com.rpgturnos.combate.client.dto;

public class SkillResponse {

    private Long id;
    private String nome;
    private String tipo;
    private Integer dano;
    private Integer custoMana;
    private Integer cooldown;

    public SkillResponse() {
    }

    public SkillResponse(Long id, String nome, String tipo, Integer dano,
                         Integer custoMana, Integer cooldown) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.dano = dano;
        this.custoMana = custoMana;
        this.cooldown = cooldown;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getDano() {
        return dano;
    }

    public Integer getCustoMana() {
        return custoMana;
    }

    public Integer getCooldown() {
        return cooldown;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDano(Integer dano) {
        this.dano = dano;
    }

    public void setCustoMana(Integer custoMana) {
        this.custoMana = custoMana;
    }

    public void setCooldown(Integer cooldown) {
        this.cooldown = cooldown;
    }
}