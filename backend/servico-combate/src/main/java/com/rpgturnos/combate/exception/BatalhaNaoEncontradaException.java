package com.rpgturnos.combate.exception;

public class BatalhaNaoEncontradaException extends RuntimeException {

    public BatalhaNaoEncontradaException(Long batalhaId) {
        super("Batalha nao encontrada: " + batalhaId);
    }
}
