package com.rpgturnos.combate.state;

import com.rpgturnos.combate.model.Batalha;
import com.rpgturnos.combate.model.StatusBatalha;

public class BatalhaFinalizadaState implements BatalhaState {

    @Override
    public void definirEstado(Batalha batalha) {

        batalha.setStatus(StatusBatalha.FINALIZADA);
    }
}
