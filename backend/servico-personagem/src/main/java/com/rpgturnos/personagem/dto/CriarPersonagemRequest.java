package com.rpgturnos.personagem.dto;

import com.rpgturnos.personagem.model.ClassePersonagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CriarPersonagemRequest {

    @NotNull
    @Positive
    private Long usuarioId;

    @NotBlank
    private String nome;

    @NotNull
    private ClassePersonagem classe;

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
}
