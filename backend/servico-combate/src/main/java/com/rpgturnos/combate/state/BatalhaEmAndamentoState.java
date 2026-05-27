package com.rpgturnos.combate.state;

import com.rpgturnos.combate.model.Batalha;

public class BatalhaEmAndamentoState implements BatalhaState {

    @Override
    public void definirEstado(Batalha batalha) {

        batalha.setStatus("EM_ANDAMENTO");
    }
}