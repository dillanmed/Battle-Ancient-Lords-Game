package com.rpgturnos.personagem.exception;

public class PersonagemNotFoundException extends RuntimeException {

    public PersonagemNotFoundException(Long id) {
        super("Personagem nao encontrado com id: " + id);
    }
}
