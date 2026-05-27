package com.rpgturnos.combate.strategy;

public class AtaqueFisicoStrategy implements AtaqueStrategy {

    @Override
    public int calcularDano() {

        return 20;
    }
}