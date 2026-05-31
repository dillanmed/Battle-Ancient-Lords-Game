package com.rpgturnos.combate.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.rpgturnos.combate.client.CharacterClient;
import com.rpgturnos.combate.client.DadosCombatePersonagemResponse;
import com.rpgturnos.combate.decorator.AtaqueBuffDecorator;
import com.rpgturnos.combate.decorator.DefesaBuffDecorator;
import com.rpgturnos.combate.decorator.DefesaDebuffDecorator;
import com.rpgturnos.combate.dto.AtaqueRequest;
import com.rpgturnos.combate.dto.HabilidadeRequest;
import com.rpgturnos.combate.event.BatalhaFinalizadaEvent;
import com.rpgturnos.combate.exception.BatalhaFinalizadaException;
import com.rpgturnos.combate.exception.BatalhaNaoEncontradaException;
import com.rpgturnos.combate.exception.HabilidadeInvalidaException;
import com.rpgturnos.combate.exception.TurnoInvalidoException;
import com.rpgturnos.combate.model.Batalha;
import com.rpgturnos.combate.model.CombatenteSnapshot;
import com.rpgturnos.combate.model.ResultadoBatalha;
import com.rpgturnos.combate.model.StatusBatalha;
import com.rpgturnos.combate.model.TipoEventoBatalha;
import com.rpgturnos.combate.model.Turno;
import com.rpgturnos.combate.rabbitmq.BatalhaProducer;
import com.rpgturnos.combate.repository.BatalhaRepository;
import com.rpgturnos.combate.strategy.DamageStrategy;
import com.rpgturnos.combate.strategy.MagicalDamageStrategy;
import com.rpgturnos.combate.strategy.PhysicalDamageStrategy;

@Service
public class BatalhaService {

    private final BatalhaRepository batalhaRepository;
    private final BatalhaProducer batalhaProducer;
    private final EventoBatalhaService eventoBatalhaService;
    private final CharacterClient characterClient;
    private final InimigoService inimigoService;
    private final DamageStrategy damageStrategy = new PhysicalDamageStrategy();
    private final DamageStrategy magicalDamageStrategy = new MagicalDamageStrategy();

    public BatalhaService(BatalhaRepository batalhaRepository,
                          BatalhaProducer batalhaProducer,
                          EventoBatalhaService eventoBatalhaService,
                          CharacterClient characterClient,
                          InimigoService inimigoService) {
        this.batalhaRepository = batalhaRepository;
        this.batalhaProducer = batalhaProducer;
        this.eventoBatalhaService = eventoBatalhaService;
        this.characterClient = characterClient;
        this.inimigoService = inimigoService;
    }

    public Batalha criarBatalha(Batalha batalha) {
        batalha.setJogador(prepararJogador(batalha));
        batalha.setFase(faseOuPadrao(batalha.getFase()));
        batalha.setInimigos(inimigoService.criarSnapshotsParaFase(batalha.getFase()));
        batalha.setInimigo(batalha.getInimigos().get(0));
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

    public Batalha atacar(Long id, AtaqueRequest request) {
        Batalha batalha = buscarBatalhaOuFalhar(id);

        validarBatalhaEmAndamento(batalha);
        validarTurnoJogador(batalha);

        CombatenteSnapshot alvo = buscarInimigoAlvo(batalha, request);
        batalha.setInimigo(alvo);

        int dano = damageStrategy.calcularDano(batalha.getJogador(), alvo);
        int novaVidaInimigo = Math.max(alvo.getVidaAtual() - dano, 0);

        alvo.setVidaAtual(novaVidaInimigo);
        registrarEvento(batalha, TipoEventoBatalha.ATAQUE,
                "Jogador causou " + dano + " de dano em " + alvo.getNome());

        if (todosInimigosDerrotados(batalha)) {
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

    public Batalha usarHabilidade(Long id, HabilidadeRequest request) {
        Batalha batalha = buscarBatalhaOuFalhar(id);

        validarBatalhaEmAndamento(batalha);
        validarTurnoJogador(batalha);
        validarHabilidade(request);
        validarMana(batalha, request);

        batalha.getJogador().setManaAtual(batalha.getJogador().getManaAtual() - request.getCustoMana());
        aplicarHabilidade(batalha, request);

        if (todosInimigosDerrotados(batalha)) {
            finalizarComVitoria(batalha);
        } else {
            passarTurnoParaInimigo(batalha);
            executarTurnoInimigo(batalha);
        }

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
        if (batalha.getStatus() == StatusBatalha.FINALIZADA) {
            throw new BatalhaFinalizadaException(batalha.getId());
        }

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
        registrarEvento(batalha, TipoEventoBatalha.TURNO_ALTERADO, "Turno alterado para INIMIGO");
    }

    private void passarTurnoParaJogador(Batalha batalha) {
        batalha.setTurnoAtual(Turno.JOGADOR);
        batalha.setRoundAtual(batalha.getRoundAtual() + 1);
        registrarEvento(batalha, TipoEventoBatalha.TURNO_ALTERADO, "Turno alterado para JOGADOR");
    }

    private void executarTurnoInimigo(Batalha batalha) {
        CombatenteSnapshot atacante = primeiroInimigoVivo(batalha);
        batalha.setInimigo(atacante);

        int dano = damageStrategy.calcularDano(atacante, batalha.getJogador());
        int novaVidaJogador = Math.max(batalha.getJogador().getVidaAtual() - dano, 0);

        batalha.getJogador().setVidaAtual(novaVidaJogador);
        registrarEvento(batalha, TipoEventoBatalha.ATAQUE,
                atacante.getNome() + " causou " + dano + " de dano");

        if (novaVidaJogador <= 0) {
            finalizarComDerrota(batalha);
        } else {
            passarTurnoParaJogador(batalha);
        }
    }

    private void executarTurnoInimigoComDefesa(Batalha batalha) {
        CombatenteSnapshot atacante = primeiroInimigoVivo(batalha);
        batalha.setInimigo(atacante);

        int dano = Math.max(damageStrategy.calcularDano(atacante, batalha.getJogador()) / 2, 1);
        int novaVidaJogador = Math.max(batalha.getJogador().getVidaAtual() - dano, 0);

        batalha.getJogador().setVidaAtual(novaVidaJogador);
        registrarEvento(batalha, TipoEventoBatalha.ATAQUE,
                atacante.getNome() + " causou " + dano + " de dano contra defesa");

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

        registrarEvento(batalha, TipoEventoBatalha.INIMIGO_DERROTADO, "Vitoria: inimigo derrotado");
        registrarEvento(batalha, TipoEventoBatalha.BATALHA_FINALIZADA, "Batalha finalizada com vitoria");
        publicarEventoFinalizacao(batalha);
    }

    private void finalizarComDerrota(Batalha batalha) {
        batalha.setStatus(StatusBatalha.FINALIZADA);
        batalha.setResultado(ResultadoBatalha.DERROTA);
        batalha.setFinalizadaEm(LocalDateTime.now());

        registrarEvento(batalha, TipoEventoBatalha.JOGADOR_DERROTADO, "Derrota: jogador derrotado");
        registrarEvento(batalha, TipoEventoBatalha.BATALHA_FINALIZADA, "Batalha finalizada com derrota");
        publicarEventoFinalizacao(batalha);
    }

    private void validarHabilidade(HabilidadeRequest request) {
        if (request == null || request.getNome() == null || request.getNome().isBlank()) {
            throw new HabilidadeInvalidaException("Habilidade invalida");
        }

        if (request.getCustoMana() == null || request.getCustoMana() < 0) {
            throw new HabilidadeInvalidaException("Custo de mana invalido");
        }
    }

    private void validarMana(Batalha batalha, HabilidadeRequest request) {
        if (batalha.getJogador().getManaAtual() < request.getCustoMana()) {
            throw new HabilidadeInvalidaException("Mana insuficiente para usar a habilidade");
        }
    }

    private void aplicarHabilidade(Batalha batalha, HabilidadeRequest request) {
        String tipo = request.getTipo() == null ? "DANO" : request.getTipo().trim().toUpperCase();

        switch (tipo) {
            case "BUFF_ATAQUE" -> {
                int bonus = valorOuPadrao(request.getDano(), 5);
                new AtaqueBuffDecorator(batalha.getJogador(), bonus).aplicar();
                registrarEvento(batalha, TipoEventoBatalha.HABILIDADE,
                        request.getNome() + " aumentou ataque do jogador em " + bonus);
            }
            case "BUFF_DEFESA" -> {
                int bonus = valorOuPadrao(request.getDano(), 5);
                new DefesaBuffDecorator(batalha.getJogador(), bonus).aplicar();
                registrarEvento(batalha, TipoEventoBatalha.HABILIDADE,
                        request.getNome() + " aumentou defesa do jogador em " + bonus);
            }
            case "DEBUFF_DEFESA" -> {
                int reducao = valorOuPadrao(request.getDano(), 5);
                new DefesaDebuffDecorator(batalha.getInimigo(), reducao).aplicar();
                registrarEvento(batalha, TipoEventoBatalha.HABILIDADE,
                        request.getNome() + " reduziu defesa do inimigo em " + reducao);
            }
            case "CURA" -> aplicarCuraDeHabilidade(batalha, request);
            case "AREA", "DANO_AREA" -> aplicarDanoEmArea(batalha, request);
            case "MAGICA", "DANO_MAGICO" -> aplicarDanoDeHabilidade(batalha, request, magicalDamageStrategy);
            case "DANO", "FISICA", "ATAQUE" -> aplicarDanoDeHabilidade(batalha, request, damageStrategy);
            default -> throw new HabilidadeInvalidaException("Tipo de habilidade invalido: " + request.getTipo());
        }
    }

    private void aplicarDanoDeHabilidade(Batalha batalha, HabilidadeRequest request, DamageStrategy strategy) {
        CombatenteSnapshot alvo = buscarInimigoAlvo(batalha, request);
        batalha.setInimigo(alvo);

        int danoBase = strategy.calcularDano(batalha.getJogador(), alvo);
        int dano = Math.max(danoBase + valorOuPadrao(request.getDano(), 0), 1);
        int novaVidaInimigo = Math.max(alvo.getVidaAtual() - dano, 0);

        alvo.setVidaAtual(novaVidaInimigo);
        registrarEvento(batalha, TipoEventoBatalha.HABILIDADE,
                request.getNome() + " causou " + dano + " de dano");
    }

    private void aplicarDanoEmArea(Batalha batalha, HabilidadeRequest request) {
        for (CombatenteSnapshot inimigo : batalha.getInimigos()) {
            if (!estaVivo(inimigo)) {
                continue;
            }

            int dano = Math.max(damageStrategy.calcularDano(batalha.getJogador(), inimigo)
                    + valorOuPadrao(request.getDano(), 0), 1);
            inimigo.setVidaAtual(Math.max(inimigo.getVidaAtual() - dano, 0));
        }

        registrarEvento(batalha, TipoEventoBatalha.HABILIDADE,
                request.getNome() + " atingiu todos os inimigos");
    }

    private void aplicarCuraDeHabilidade(Batalha batalha, HabilidadeRequest request) {
        int cura = valorOuPadrao(request.getDano(), 20);
        int novaVidaJogador = Math.min(batalha.getJogador().getVidaAtual() + cura,
                batalha.getJogador().getVidaMaxima());

        batalha.getJogador().setVidaAtual(novaVidaJogador);
        registrarEvento(batalha, TipoEventoBatalha.HABILIDADE,
                request.getNome() + " recuperou " + cura + " de vida");
    }

    private int valorOuPadrao(Integer valor, int padrao) {
        return valor == null ? padrao : valor;
    }

    private CombatenteSnapshot buscarInimigoAlvo(Batalha batalha, AtaqueRequest request) {
        Long inimigoId = request == null ? null : request.getInimigoId();
        return buscarInimigoAlvoPorId(batalha, inimigoId);
    }

    private CombatenteSnapshot buscarInimigoAlvo(Batalha batalha, HabilidadeRequest request) {
        Long inimigoId = request == null ? null : request.getInimigoId();
        return buscarInimigoAlvoPorId(batalha, inimigoId);
    }

    private CombatenteSnapshot buscarInimigoAlvoPorId(Batalha batalha, Long inimigoId) {
        if (inimigoId != null) {
            return batalha.getInimigos()
                    .stream()
                    .filter(inimigo -> inimigoId.equals(inimigo.getId())
                            || inimigoId.equals(inimigo.getReferenciaOriginalId()))
                    .findFirst()
                    .filter(this::estaVivo)
                    .orElseThrow(() -> new TurnoInvalidoException("Inimigo invalido ou derrotado"));
        }

        return primeiroInimigoVivo(batalha);
    }

    private CombatenteSnapshot primeiroInimigoVivo(Batalha batalha) {
        return batalha.getInimigos()
                .stream()
                .filter(this::estaVivo)
                .findFirst()
                .orElseThrow(() -> new TurnoInvalidoException("Nao ha inimigos vivos"));
    }

    private boolean todosInimigosDerrotados(Batalha batalha) {
        return batalha.getInimigos()
                .stream()
                .noneMatch(this::estaVivo);
    }

    private boolean estaVivo(CombatenteSnapshot inimigo) {
        return inimigo != null && inimigo.getVidaAtual() != null && inimigo.getVidaAtual() > 0;
    }

    private Integer faseOuPadrao(Integer fase) {
        return fase == null ? 1 : fase;
    }

    private CombatenteSnapshot prepararJogador(Batalha batalha) {
        if (batalha.getJogador() != null) {
            return batalha.getJogador();
        }

        try {
            DadosCombatePersonagemResponse personagem = characterClient.buscarDadosCombate(batalha.getPersonagemId());

            return CombatenteSnapshot.builder()
                    .referenciaOriginalId(personagem.getId())
                    .nome(personagem.getNome())
                    .tipo("JOGADOR")
                    .vidaMaxima(personagem.getVidaMaxima())
                    .vidaAtual(personagem.getVidaMaxima())
                    .manaMaxima(personagem.getManaMaxima())
                    .manaAtual(personagem.getManaMaxima())
                    .ataque(personagem.getAtaque())
                    .defesa(personagem.getDefesa())
                    .nivel(personagem.getNivel())
                    .build();
        } catch (RestClientException exception) {
            return criarJogadorPadrao(batalha.getPersonagemId());
        }
    }

    private CombatenteSnapshot criarJogadorPadrao(Long personagemId) {
        return CombatenteSnapshot.builder()
                .referenciaOriginalId(personagemId)
                .nome("Aventureiro")
                .tipo("JOGADOR")
                .vidaMaxima(120)
                .vidaAtual(120)
                .manaMaxima(40)
                .manaAtual(40)
                .ataque(30)
                .defesa(12)
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
