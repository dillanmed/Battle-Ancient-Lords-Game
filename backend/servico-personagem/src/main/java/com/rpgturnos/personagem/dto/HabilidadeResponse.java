package com.rpgturnos.personagem.dto;

import com.rpgturnos.personagem.model.ClassePersonagem;
import com.rpgturnos.personagem.model.Habilidade;
import com.rpgturnos.personagem.model.TipoHabilidade;

public class HabilidadeResponse {

    private Long id;
    private String nome;
    private String descricao;
    private ClassePersonagem classePermitida;
    private TipoHabilidade tipo;
    private Integer custoMana;
    private Integer poder;
    private Integer nivelNecessario;

    public HabilidadeResponse() {
    }

    public HabilidadeResponse(Long id, String nome, String descricao, ClassePersonagem classePermitida,
                              TipoHabilidade tipo, Integer custoMana, Integer poder, Integer nivelNecessario) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.classePermitida = classePermitida;
        this.tipo = tipo;
        this.custoMana = custoMana;
        this.poder = poder;
        this.nivelNecessario = nivelNecessario;
    }

    public static HabilidadeResponse from(Habilidade habilidade) {
        return new HabilidadeResponse(habilidade.getId(), habilidade.getNome(), habilidade.getDescricao(),
                habilidade.getClassePermitida(), habilidade.getTipo(), habilidade.getCustoMana(),
                habilidade.getPoder(), habilidade.getNivelNecessario());
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

    public ClassePersonagem getClassePermitida() {
        return classePermitida;
    }

    public TipoHabilidade getTipo() {
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
}
