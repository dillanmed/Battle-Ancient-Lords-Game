package com.rpgturnos.combate.client;

public class HabilidadePersonagemResponse {

    private Long id;
    private String nome;
    private String descricao;
    private String classePermitida;
    private String tipo;
    private Integer custoMana;
    private Integer poder;
    private Integer nivelNecessario;

    public HabilidadePersonagemResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getClassePermitida() {
        return classePermitida;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getCustoMana() {
        return custoMana;
    }

    public Integer getPoder() {
        return poder;
    }

    public Integer getNivelNecessario() {
        return nivelNecessario;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setClassePermitida(String classePermitida) {
        this.classePermitida = classePermitida;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCustoMana(Integer custoMana) {
        this.custoMana = custoMana;
    }

    public void setPoder(Integer poder) {
        this.poder = poder;
    }

    public void setNivelNecessario(Integer nivelNecessario) {
        this.nivelNecessario = nivelNecessario;
    }
}
