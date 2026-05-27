package com.rpgturnos.combate.service;

import com.rpgturnos.combate.event.BatalhaFinalizadaEvent;
import com.rpgturnos.combate.model.Batalha;
import com.rpgturnos.combate.rabbitmq.BatalhaProducer;
import com.rpgturnos.combate.repository.BatalhaRepository;
import com.rpgturnos.combate.state.BatalhaEmAndamentoState;
import com.rpgturnos.combate.state.BatalhaFinalizadaState;
import com.rpgturnos.combate.state.BatalhaState;
import com.rpgturnos.combate.strategy.AtaqueFisicoStrategy;
import com.rpgturnos.combate.strategy.AtaqueStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatalhaService {

    private final BatalhaRepository batalhaRepository;
    private final BatalhaProducer batalhaProducer;

    public BatalhaService(BatalhaRepository batalhaRepository, BatalhaProducer batalhaProducer) {
        this.batalhaRepository = batalhaRepository;
        this.batalhaProducer = batalhaProducer;
    }

    public Batalha criarBatalha(Batalha batalha) {
        batalha.setVidaPersonagem(100);
        batalha.setVidaInimigo(100);
        batalha.setTurnoAtual(1);

        BatalhaState batalhaState = new BatalhaEmAndamentoState();
        batalhaState.definirEstado(batalha);

        batalha.setResultado("PENDENTE");

        return batalhaRepository.save(batalha);
    }

    public Optional<Batalha> buscarPorId(Long id) {
        return batalhaRepository.findById(id);
    }

    public List<Batalha> listarBatalhas() {
        return batalhaRepository.findAll();
    }

    public Batalha atacar(Long id) {
        Batalha batalha = batalhaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batalha não encontrada"));

        if ("FINALIZADA".equals(batalha.getStatus())) {
            throw new RuntimeException("Essa batalha já foi finalizada");
        }

        AtaqueStrategy ataqueStrategy = new AtaqueFisicoStrategy();
        int dano = ataqueStrategy.calcularDano();

        batalha.setVidaInimigo(batalha.getVidaInimigo() - dano);

        if (batalha.getVidaInimigo() <= 0) {
            batalha.setVidaInimigo(0);

            BatalhaState batalhaState = new BatalhaFinalizadaState();
            batalhaState.definirEstado(batalha);

            batalha.setResultado("VITORIA");

            BatalhaFinalizadaEvent event = new BatalhaFinalizadaEvent(
                    batalha.getId(),
                    batalha.getUsuarioId(),
                    batalha.getPersonagemId(),
                    batalha.getInimigoNome(),
                    batalha.getResultado(),
                    batalha.getVidaPersonagem()
            );

            batalhaProducer.publicarBatalhaFinalizada(event);
        }

        return batalhaRepository.save(batalha);
    }

    public Batalha defender(Long id) {
        Batalha batalha = batalhaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batalha não encontrada"));

        if ("FINALIZADA".equals(batalha.getStatus())) {
            throw new RuntimeException("Essa batalha já foi finalizada");
        }

        batalha.setStatus("DEFENDENDO");

        return batalhaRepository.save(batalha);
    }

    public Batalha finalizarBatalha(Long id) {
        Batalha batalha = batalhaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batalha não encontrada"));

        BatalhaState batalhaState = new BatalhaFinalizadaState();
        batalhaState.definirEstado(batalha);

        if ("PENDENTE".equals(batalha.getResultado())) {
            batalha.setResultado("FINALIZADA_MANUALMENTE");
        }

        BatalhaFinalizadaEvent event = new BatalhaFinalizadaEvent(
                batalha.getId(),
                batalha.getUsuarioId(),
                batalha.getPersonagemId(),
                batalha.getInimigoNome(),
                batalha.getResultado(),
                batalha.getVidaPersonagem()
        );

        batalhaProducer.publicarBatalhaFinalizada(event);

        return batalhaRepository.save(batalha);
    }
}
