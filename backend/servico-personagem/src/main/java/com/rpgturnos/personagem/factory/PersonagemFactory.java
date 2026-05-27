package com.rpgturnos.personagem.factory;

import com.rpgturnos.personagem.model.ClassePersonagem;
import com.rpgturnos.personagem.model.Personagem;

public final class PersonagemFactory {

    private PersonagemFactory() {
    }

    public static Personagem criar(Long usuarioId, String nome, ClassePersonagem classe) {
        return switch (classe) {
            case GUERREIRO -> criarGuerreiro(usuarioId, nome);
            case MAGO -> criarMago(usuarioId, nome);
            case ARQUEIRO -> criarArqueiro(usuarioId, nome);
        };
    }

    private static Personagem criarGuerreiro(Long usuarioId, String nome) {
        return Personagem.builder()
                .usuarioId(usuarioId)
                .nome(nome)
                .classe(ClassePersonagem.GUERREIRO)
                .nivel(1)
                .experiencia(0)
                .vidaMaxima(120)
                .manaMaxima(30)
                .ataque(18)
                .defesa(16)
                .forca(18)
                .inteligencia(6)
                .agilidade(10)
                .build();
    }

    private static Personagem criarMago(Long usuarioId, String nome) {
        return Personagem.builder()
                .usuarioId(usuarioId)
                .nome(nome)
                .classe(ClassePersonagem.MAGO)
                .nivel(1)
                .experiencia(0)
                .vidaMaxima(70)
                .manaMaxima(120)
                .ataque(12)
                .defesa(8)
                .forca(6)
                .inteligencia(20)
                .agilidade(10)
                .build();
    }

    private static Personagem criarArqueiro(Long usuarioId, String nome) {
        return Personagem.builder()
                .usuarioId(usuarioId)
                .nome(nome)
                .classe(ClassePersonagem.ARQUEIRO)
                .nivel(1)
                .experiencia(0)
                .vidaMaxima(90)
                .manaMaxima(60)
                .ataque(16)
                .defesa(12)
                .forca(12)
                .inteligencia(10)
                .agilidade(18)
                .build();
    }
}
