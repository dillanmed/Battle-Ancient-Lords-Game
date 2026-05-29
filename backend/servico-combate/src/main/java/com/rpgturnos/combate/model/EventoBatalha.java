package com.rpgturnos.combate.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoBatalha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long batalhaId;

    @Enumerated(EnumType.STRING)
    private TipoEventoBatalha tipo;

    private String descricao;

    private Integer round;

    private LocalDateTime criadoEm;
}