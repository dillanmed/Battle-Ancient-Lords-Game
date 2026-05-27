package com.rpgturnos.combate.state;

import com.rpgturnos.combate.model.Batalha;

public class BatalhaFinalizadaState implements BatalhaState {

    @Override
    public void definirEstado(Batalha batalha) {

        batalha.setStatus("FINALIZADA");
    }
}