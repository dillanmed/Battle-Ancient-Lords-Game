package com.rpgturnos.autenticacao.observer;

import com.rpgturnos.autenticacao.modelo.HistoricoPartida;
import com.rpgturnos.autenticacao.modelo.PerfilHistoricoUsuario;
import com.rpgturnos.autenticacao.repositorio.PerfilHistoricoUsuarioRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PerfilHistoricoObserver implements ObservadorHistoricoUsuario {

    private final PerfilHistoricoUsuarioRepository repository;

    public PerfilHistoricoObserver(PerfilHistoricoUsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void atualizar(HistoricoPartida partida) {
        PerfilHistoricoUsuario perfil = repository.findById(partida.getUsuarioId())
                .orElseGet(() -> new PerfilHistoricoUsuario(partida.getUsuarioId()));

        perfil.registrar(partida);
        repository.save(perfil);
    }
}
