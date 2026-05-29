package com.rpgturnos.combate.exception;

public class BatalhaFinalizadaException extends RuntimeException {

    public BatalhaFinalizadaException(Long batalhaId) {
        super("Batalha ja finalizada: " + batalhaId);
    }
}
