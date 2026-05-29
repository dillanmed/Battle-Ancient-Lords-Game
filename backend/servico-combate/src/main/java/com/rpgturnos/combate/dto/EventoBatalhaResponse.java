package com.rpgturnos.combate.dto;

import com.rpgturnos.combate.model.TipoEventoBatalha;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventoBatalhaResponse {

    private Long id;
    private Long batalhaId;
    private TipoEventoBatalha tipo;
    private String descricao;
    private Integer round;
    private LocalDateTime criadoEm;
}