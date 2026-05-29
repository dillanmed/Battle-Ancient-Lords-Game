package com.rpgturnos.combate.dto;

import com.rpgturnos.combate.model.ResultadoBatalha;
import com.rpgturnos.combate.model.StatusBatalha;
import com.rpgturnos.combate.model.Turno;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BatalhaResponse {

    private Long id;
    private StatusBatalha status;
    private Turno turnoAtual;
    private ResultadoBatalha resultado;
    private Integer roundAtual;

    private String jogadorNome;
    private Integer jogadorVidaAtual;
    private Integer jogadorManaAtual;

    private String inimigoNome;
    private Integer inimigoVidaAtual;
}