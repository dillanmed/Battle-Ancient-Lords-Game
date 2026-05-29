package com.rpgturnos.combate.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rpgturnos.combate.event.BatalhaFinalizadaEvent;
import com.rpgturnos.combate.exception.BatalhaFinalizadaException;
import com.rpgturnos.combate.exception.BatalhaNaoEncontradaException;
import com.rpgturnos.combate.exception.TurnoInvalidoException;
import com.rpgturnos.combate.factory.GoblinFactory;
import com.rpgturnos.combate.factory.InimigoFactory;
import com.rpgturnos.combate.model.Batalha;
import com.rpgturnos.combate.model.CombatenteSnapshot;
import com.rpgturnos.combate.model.ResultadoBatalha;
import com.rpgturnos.combate.model.StatusBatalha;
import com.rpgturnos.combate.model.TipoEventoBatalha;
import com.rpgturnos.combate.model.Turno;
import com.rpgturnos.combate.rabbitmq.BatalhaProducer;
import com.rpgturnos.combate.repository.BatalhaRepository;
import com.rpgturnos.combate.strategy.DamageStrategy;
import com.rpgturnos.combate.strategy.PhysicalDamageStrategy;

@Service
public class BatalhaService {

    private final BatalhaRepository batalhaRepository;
    private final BatalhaProducer batalhaProducer;
    private final EventoBatalhaService eventoBatalhaService;
    private final InimigoFactory inimigoFactory = new GoblinFactory();
    private final DamageStrategy damageStrategy = new PhysicalDamageStrategy();

    public BatalhaService(BatalhaRepository batalhaRepository,
                          BatalhaProducer batalhaProducer,
                          EventoBatalhaService eventoBatalhaService) {
        this.batalhaRepository = batalhaRepository;
        this.batalhaProducer = batalhaProducer;
        this.eventoBatalhaService = eventoBatalhaService;
    }

    public Batalha criarBatalha(Batalha batalha) {
        batalha.setJogador(prepararJogador(batalha));
        batalha.setInimigo(inimigoFactory.criar());
        batalha.setStatus(StatusBatalha.CRIADA);
        batalha.setTurnoAtual(Turno.JOGADOR);
        batalha.setResultado(ResultadoBatalha.PENDENTE);
        batalha.setRoundAtual(1);
        batalha.setCriadaEm(LocalDateTime.now());

        Batalha batalhaSalva = batalhaRepository.save(batalha);
        registrarEvento(batalhaSalva, TipoEventoBatalha.BATALHA_CRIADA, "Batalha criada");

        return batalhaSalva;
    }

    public Optional<Batalha> buscarPorId(Long id) {
        return batalhaRepository.findById(id);
    }

    public List<Batalha> listarBatalhas() {
        return batalhaRepository.findAll();
    }

    public Batalha iniciarBatalha(Long id) {
        Batalha batalha = buscarBatalhaOuFalhar(id);

        if (batalha.getStatus() == StatusBatalha.FINALIZADA) {
            throw new BatalhaFinalizadaException(id);
        }

        batalha.setStatus(StatusBatalha.EM_ANDAMENTO);
        batalha.setTurnoAtual(Turno.JOGADOR);

        Batalha batalhaSalva = batalhaRepository.save(batalha);
        registrarEvento(batalhaSalva, TipoEventoBatalha.BATALHA_INICIADA, "Batalha iniciada");

        return batalhaSalva;
    }

    public Batalha atacar(Long id) {
        Batalha batalha = buscarBatalhaOuFalhar(id);

        validarBatalhaEmAndamento(batalha);
        validarTurnoJogador(batalha);

        int dano = damageStrategy.calcularDano(batalha.getJogador(), batalha.getInimigo());
        int novaVidaInimigo = Math.max(batalha.getInimigo().getVidaAtual() - dano, 0);

        batalha.getInimigo().setVidaAtual(novaVidaInimigo);
        registrarEvento(batalha, TipoEventoBatalha.ATAQUE, "Jogador causou " + dano + " de dano");

        if (novaVidaInimigo <= 0) {
            finalizarComVitoria(batalha);
        } else {
            passarTurnoParaInimigo(batalha);
            executarTurnoInimigo(batalha);
        }

        return batalhaRepository.save(batalha);
    }

    public Batalha defender(Long id) {
        Batalha batalha = buscarBatalhaOuFalhar(id);

        validarBatalhaEmAndamento(batalha);
        validarTurnoJogador(batalha);

        registrarEvento(batalha, TipoEventoBatalha.DEFESA, "Jogador assumiu postura defensiva");
        passarTurnoParaInimigo(batalha);
        executarTurnoInimigoComDefesa(batalha);

        return batalhaRepository.save(batalha);
    }

    public Batalha finalizarBatalha(Long id) {
        Batalha batalha = buscarBatalhaOuFalhar(id);

        batalha.setStatus(StatusBatalha.FINALIZADA);
        batalha.setResultado(ResultadoBatalha.CANCELADA);
        batalha.setFinalizadaEm(LocalDateTime.now());

        registrarEvento(batalha, TipoEventoBatalha.BATALHA_FINALIZADA, "Batalha finalizada manualmente");
        publicarEventoFinalizacao(batalha);

        return batalhaRepository.save(batalha);
    }

    private Batalha buscarBatalhaOuFalhar(Long id) {
        return batalhaRepository.findById(id)
                .orElseThrow(() -> new BatalhaNaoEncontradaException(id));
    }

    private void validarBatalhaEmAndamento(Batalha batalha) {
        if (batalha.getStatus() != StatusBatalha.EM_ANDAMENTO) {
            throw new TurnoInvalidoException("A batalha nao esta em andamento");
        }
    }

    private void validarTurnoJogador(Batalha batalha) {
        if (batalha.getTurnoAtual() != Turno.JOGADOR) {
            throw new TurnoInvalidoException("Nao e o turno do jogador");
        }
    }

    private void passarTurnoParaInimigo(Batalha batalha) {
        batalha.setTurnoAtual(Turno.INIMIGO);
    }

    private void passarTurnoParaJogador(Batalha batalha) {
        batalha.setTurnoAtual(Turno.JOGADOR);
        batalha.setRoundAtual(batalha.getRoundAtual() + 1);
    }

    private void executarTurnoInimigo(Batalha batalha) {
        int dano = damageStrategy.calcularDano(batalha.getInimigo(), batalha.getJogador());
        int novaVidaJogador = Math.max(batalha.getJogador().getVidaAtual() - dano, 0);

        batalha.getJogador().setVidaAtual(novaVidaJogador);
        registrarEvento(batalha, TipoEventoBatalha.ATAQUE, "Inimigo causou " + dano + " de dano");

        if (novaVidaJogador <= 0) {
            finalizarComDerrota(batalha);
        } else {
            passarTurnoParaJogador(batalha);
        }
    }

    private void executarTurnoInimigoComDefesa(Batalha batalha) {
        int dano = Math.max(damageStrategy.calcularDano(batalha.getInimigo(), batalha.getJogador()) / 2, 1);
        int novaVidaJogador = Math.max(batalha.getJogador().getVidaAtual() - dano, 0);

        batalha.getJogador().setVidaAtual(novaVidaJogador);
        registrarEvento(batalha, TipoEventoBatalha.ATAQUE, "Inimigo causou " + dano + " de dano contra defesa");

        if (novaVidaJogador <= 0) {
            finalizarComDerrota(batalha);
        } else {
            passarTurnoParaJogador(batalha);
        }
    }

    private void finalizarComVitoria(Batalha batalha) {
        batalha.setStatus(StatusBatalha.FINALIZADA);
        batalha.setResultado(ResultadoBatalha.VITORIA);
        batalha.setFinalizadaEm(LocalDateTime.now());

        registrarEvento(batalha, TipoEventoBatalha.INIMIGO_DERROTADO, "Inimigo derrotado");
        registrarEvento(batalha, TipoEventoBatalha.BATALHA_FINALIZADA, "Batalha finalizada com vitoria");
        publicarEventoFinalizacao(batalha);
    }

    private void finalizarComDerrota(Batalha batalha) {
        batalha.setStatus(StatusBatalha.FINALIZADA);
        batalha.setResultado(ResultadoBatalha.DERROTA);
        batalha.setFinalizadaEm(LocalDateTime.now());

        registrarEvento(batalha, TipoEventoBatalha.JOGADOR_DERROTADO, "Jogador derrotado");
        registrarEvento(batalha, TipoEventoBatalha.BATALHA_FINALIZADA, "Batalha finalizada com derrota");
        publicarEventoFinalizacao(batalha);
    }

    private CombatenteSnapshot prepararJogador(Batalha batalha) {
        if (batalha.getJogador() != null) {
            return batalha.getJogador();
        }

        return CombatenteSnapshot.builder()
                .referenciaOriginalId(batalha.getPersonagemId())
                .nome("Jogador")
                .tipo("JOGADOR")
                .vidaMaxima(100)
                .vidaAtual(100)
                .manaMaxima(50)
                .manaAtual(50)
                .ataque(20)
                .defesa(10)
                .nivel(1)
                .build();
    }

    private void registrarEvento(Batalha batalha, TipoEventoBatalha tipo, String descricao) {
        if (batalha.getId() != null) {
            eventoBatalhaService.registrar(batalha.getId(), tipo, descricao, batalha.getRoundAtual());
        }
    }

    private void publicarEventoFinalizacao(Batalha batalha) {
        BatalhaFinalizadaEvent event = new BatalhaFinalizadaEvent(
                batalha.getId(),
                batalha.getUsuarioId(),
                batalha.getPersonagemId(),
                batalha.getInimigo().getNome(),
                batalha.getResultado().name(),
                batalha.getJogador().getVidaAtual()
        );

        batalhaProducer.publicarBatalhaFinalizada(event);
    }
}
