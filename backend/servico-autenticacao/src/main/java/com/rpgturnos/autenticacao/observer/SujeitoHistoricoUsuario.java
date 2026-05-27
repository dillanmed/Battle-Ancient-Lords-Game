package com.rpgturnos.autenticacao.observer;

import com.rpgturnos.autenticacao.modelo.HistoricoPartida;

public interface SujeitoHistoricoUsuario {

    void adicionarObservador(ObservadorHistoricoUsuario observador);

    void removerObservador(ObservadorHistoricoUsuario observador);

    void notificarObservadores(HistoricoPartida partida);
}
