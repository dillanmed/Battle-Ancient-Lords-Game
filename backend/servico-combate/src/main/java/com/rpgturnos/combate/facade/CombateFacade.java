package com.rpgturnos.combate.facade;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.rpgturnos.combate.dto.HabilidadeRequest;
import com.rpgturnos.combate.model.Batalha;
import com.rpgturnos.combate.model.EventoBatalha;
import com.rpgturnos.combate.service.BatalhaService;
import com.rpgturnos.combate.service.EventoBatalhaService;

@Component
public class CombateFacade {

    private final BatalhaService batalhaService;
    private final EventoBatalhaService eventoBatalhaService;

    public CombateFacade(BatalhaService batalhaService, EventoBatalhaService eventoBatalhaService) {
        this.batalhaService = batalhaService;
        this.eventoBatalhaService = eventoBatalhaService;
    }

    public Batalha criarBatalha(Batalha batalha) {
        return batalhaService.criarBatalha(batalha);
    }

    public Batalha iniciarBatalha(Long id) {
        return batalhaService.iniciarBatalha(id);
    }

    public Optional<Batalha> buscarBatalha(Long id) {
        return batalhaService.buscarPorId(id);
    }

    public List<Batalha> listarBatalhas() {
        return batalhaService.listarBatalhas();
    }

    public Batalha atacar(Long id) {
        return batalhaService.atacar(id);
    }

    public Batalha defender(Long id) {
        return batalhaService.defender(id);
    }

    public Batalha usarHabilidade(Long id, HabilidadeRequest request) {
        return batalhaService.usarHabilidade(id, request);
    }

    public Batalha finalizarBatalha(Long id) {
        return batalhaService.finalizarBatalha(id);
    }

    public List<EventoBatalha> listarEventos(Long batalhaId) {
        return eventoBatalhaService.listarPorBatalha(batalhaId);
    }
}
