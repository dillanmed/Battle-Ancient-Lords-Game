package com.rpgturnos.combate.facade;

import com.rpgturnos.combate.model.Batalha;
import com.rpgturnos.combate.service.BatalhaService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CombateFacade {

    private final BatalhaService batalhaService;

    public CombateFacade(BatalhaService batalhaService) {
        this.batalhaService = batalhaService;
    }

    public Batalha iniciarBatalha(Batalha batalha) {
        return batalhaService.criarBatalha(batalha);
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

    public Batalha finalizarBatalha(Long id) {
        return batalhaService.finalizarBatalha(id);
    }
}