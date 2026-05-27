package com.rpgturnos.personagem.exception;

public class HabilidadeNotFoundException extends RuntimeException {

    public HabilidadeNotFoundException(Long id) {
        super("Habilidade nao encontrada com id: " + id);
    }
}
