package com.rpgturnos.personagem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AtualizarExperienciaRequest {

    @NotNull
    @Positive
    private Integer experiencia;

    public Integer getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(Integer experiencia) {
        this.experiencia = experiencia;
    }
}
