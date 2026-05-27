package com.rpgturnos.personagem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "habilidades")
public class Habilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private ClassePersonagem classePermitida;

    @Enumerated(EnumType.STRING)
    private TipoHabilidade tipo;

    private Integer custoMana;

    private Integer poder;

    private Integer nivelNecessario;

    public Habilidade() {
    }

    public Habilidade(Long id, String nome, String descricao, ClassePersonagem classePermitida, TipoHabilidade tipo,
                      Integer custoMana, Integer poder, Integer nivelNecessario) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.classePermitida = classePermitida;
        this.tipo = tipo;
        this.custoMana = custoMana;
        this.poder = poder;
        this.nivelNecessario = nivelNecessario;
    }

    public static HabilidadeBuilder builder() {
        return new HabilidadeBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ClassePersonagem getClassePermitida() {
        return classePermitida;
    }

    public void setClassePermitida(ClassePersonagem classePermitida) {
        this.classePermitida = classePermitida;
    }

    public TipoHabilidade getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabilidade tipo) {
        this.tipo = tipo;
    }

    public Integer getCustoMana() {
        return custoMana;
    }

    public void setCustoMana(Integer custoMana) {
        this.custoMana = custoMana;
    }

    public Integer getPoder() {
        return poder;
    }

    public void setPoder(Integer poder) {
        this.poder = poder;
    }

    public Integer getNivelNecessario() {
        return nivelNecessario;
    }

    public void setNivelNecessario(Integer nivelNecessario) {
        this.nivelNecessario = nivelNecessario;
    }

    public static class HabilidadeBuilder {

        private Long id;
        private String nome;
        private String descricao;
        private ClassePersonagem classePermitida;
        private TipoHabilidade tipo;
        private Integer custoMana;
        private Integer poder;
        private Integer nivelNecessario;

        public HabilidadeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public HabilidadeBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public HabilidadeBuilder descricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public HabilidadeBuilder classePermitida(ClassePersonagem classePermitida) {
            this.classePermitida = classePermitida;
            return this;
        }

        public HabilidadeBuilder tipo(TipoHabilidade tipo) {
            this.tipo = tipo;
            return this;
        }

        public HabilidadeBuilder custoMana(Integer custoMana) {
            this.custoMana = custoMana;
            return this;
        }

        public HabilidadeBuilder poder(Integer poder) {
            this.poder = poder;
            return this;
        }

        public HabilidadeBuilder nivelNecessario(Integer nivelNecessario) {
            this.nivelNecessario = nivelNecessario;
            return this;
        }

        public Habilidade build() {
            return new Habilidade(id, nome, descricao, classePermitida, tipo, custoMana, poder, nivelNecessario);
        }
    }
}
