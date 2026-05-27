package com.rpgturnos.autenticacao.servico;

import com.rpgturnos.autenticacao.dto.PartidaHistoricoResponse;
import com.rpgturnos.autenticacao.dto.PerfilResponse;
import com.rpgturnos.autenticacao.modelo.HistoricoPartida;
import com.rpgturnos.autenticacao.modelo.PerfilHistoricoUsuario;
import com.rpgturnos.autenticacao.modelo.Usuario;
import com.rpgturnos.autenticacao.observer.ObservadorHistoricoUsuario;
import com.rpgturnos.autenticacao.observer.SujeitoHistoricoUsuario;
import com.rpgturnos.autenticacao.repositorio.HistoricoPartidaRepository;
import com.rpgturnos.autenticacao.repositorio.PerfilHistoricoUsuarioRepository;
import com.rpgturnos.combate.event.BatalhaFinalizadaEvent;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoricoUsuarioService implements SujeitoHistoricoUsuario {

    private final HistoricoPartidaRepository historicoPartidaRepository;
    private final PerfilHistoricoUsuarioRepository perfilHistoricoUsuarioRepository;
    private final List<ObservadorHistoricoUsuario> observadores = new CopyOnWriteArrayList<>();

    public HistoricoUsuarioService(
            HistoricoPartidaRepository historicoPartidaRepository,
            PerfilHistoricoUsuarioRepository perfilHistoricoUsuarioRepository,
            List<ObservadorHistoricoUsuario> observadores
    ) {
        this.historicoPartidaRepository = historicoPartidaRepository;
        this.perfilHistoricoUsuarioRepository = perfilHistoricoUsuarioRepository;
        this.observadores.addAll(observadores);
    }

    @Transactional
    public void registrarPartidaFinalizada(BatalhaFinalizadaEvent event) {
        if (event.getUsuarioId() == null || event.getBatalhaId() == null) {
            return;
        }

        if (historicoPartidaRepository.findByBatalhaId(event.getBatalhaId()).isPresent()) {
            return;
        }

        HistoricoPartida partida = historicoPartidaRepository.save(new HistoricoPartida(
                event.getUsuarioId(),
                event.getBatalhaId(),
                event.getPersonagemId(),
                valorOuPadrao(event.getInimigoNome(), "Inimigo desconhecido"),
                valorOuPadrao(event.getResultado(), "FINALIZADA"),
                event.getVidaFinalPersonagem()
        ));

        notificarObservadores(partida);
    }

    @Transactional(readOnly = true)
    public PerfilResponse montarPerfil(Usuario usuario) {
        PerfilHistoricoUsuario resumo = buscarOuCriarResumo(usuario.getId());
        List<PartidaHistoricoResponse> historico = listarHistorico(usuario.getId());

        return new PerfilResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                resumo.getNivelConta(),
                resumo.getTotalPartidas(),
                resumo.getVitorias(),
                resumo.getDerrotas(),
                resumo.getAtualizadoEm(),
                historico
        );
    }

    @Transactional(readOnly = true)
    public List<PartidaHistoricoResponse> listarHistorico(Long usuarioId) {
        return historicoPartidaRepository.findByUsuarioIdOrderByFinalizadaEmDesc(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void adicionarObservador(ObservadorHistoricoUsuario observador) {
        observadores.add(observador);
    }

    @Override
    public void removerObservador(ObservadorHistoricoUsuario observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(HistoricoPartida partida) {
        observadores.forEach(observador -> observador.atualizar(partida));
    }

    private PerfilHistoricoUsuario buscarOuCriarResumo(Long usuarioId) {
        return perfilHistoricoUsuarioRepository.findById(usuarioId)
                .orElseGet(() -> new PerfilHistoricoUsuario(usuarioId));
    }

    private PartidaHistoricoResponse toResponse(HistoricoPartida partida) {
        Instant finalizadaEm = partida.getFinalizadaEm();

        return new PartidaHistoricoResponse(
                partida.getId(),
                partida.getBatalhaId(),
                partida.getPersonagemId(),
                partida.getInimigoNome(),
                partida.getResultado(),
                partida.getVidaFinalPersonagem(),
                finalizadaEm
        );
    }

    private String valorOuPadrao(String valor, String padrao) {
        if (valor == null || valor.isBlank()) {
            return padrao;
        }

        return valor.trim();
    }
}
