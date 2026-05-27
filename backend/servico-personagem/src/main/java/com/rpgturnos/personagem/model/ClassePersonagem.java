package com.rpgturnos.personagem.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ClassePersonagem {
    GUERREIRO,
    MAGO,
    ARQUEIRO;

    @JsonCreator
    public static ClassePersonagem from(String valor) {
        if (valor == null) {
            return null;
        }

        return switch (valor.trim().toUpperCase()) {
            case "GUERREIRO", "WARRIOR" -> GUERREIRO;
            case "MAGO", "MAGE" -> MAGO;
            case "ARQUEIRO", "ARCHER" -> ARQUEIRO;
            default -> throw new IllegalArgumentException("Classe de personagem invalida: " + valor);
        };
    }
}
