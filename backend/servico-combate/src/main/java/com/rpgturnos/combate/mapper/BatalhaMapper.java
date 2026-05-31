package com.rpgturnos.combate.mapper;

import com.rpgturnos.combate.dto.BatalhaResponse;
import com.rpgturnos.combate.dto.CriarBatalhaRequest;
import com.rpgturnos.combate.dto.EventoBatalhaResponse;
import com.rpgturnos.combate.dto.InimigoBatalhaResponse;
import com.rpgturnos.combate.model.Batalha;
import com.rpgturnos.combate.model.CombatenteSnapshot;
import com.rpgturnos.combate.model.EventoBatalha;

import java.util.List;

public final class BatalhaMapper {

    private BatalhaMapper() {
    }

    public static Batalha toEntity(CriarBatalhaRequest request) {
        return Batalha.builder()
                .usuarioId(request.getUsuarioId())
                .personagemId(request.getPersonagemId())
                .fase(request.getFase())
                .build();
    }

    public static BatalhaResponse toResponse(Batalha batalha) {
        return BatalhaResponse.builder()
                .id(batalha.getId())
                .status(batalha.getStatus())
                .turnoAtual(batalha.getTurnoAtual())
                .resultado(batalha.getResultado())
                .roundAtual(batalha.getRoundAtual())
                .jogadorNome(nome(batalha.getJogador()))
                .jogadorVidaAtual(vidaAtual(batalha.getJogador()))
                .jogadorManaAtual(manaAtual(batalha.getJogador()))
                .inimigoNome(nome(batalha.getInimigo()))
                .inimigoVidaAtual(vidaAtual(batalha.getInimigo()))
                .inimigos(toInimigosResponse(batalha.getInimigos()))
                .build();
    }

    public static EventoBatalhaResponse toResponse(EventoBatalha evento) {
        return EventoBatalhaResponse.builder()
                .id(evento.getId())
                .batalhaId(evento.getBatalhaId())
                .tipo(evento.getTipo())
                .descricao(evento.getDescricao())
                .round(evento.getRound())
                .criadoEm(evento.getCriadoEm())
                .build();
    }

    private static String nome(CombatenteSnapshot combatente) {
        return combatente == null ? null : combatente.getNome();
    }

    private static Integer vidaAtual(CombatenteSnapshot combatente) {
        return combatente == null ? null : combatente.getVidaAtual();
    }

    private static Integer manaAtual(CombatenteSnapshot combatente) {
        return combatente == null ? null : combatente.getManaAtual();
    }

    private static List<InimigoBatalhaResponse> toInimigosResponse(List<CombatenteSnapshot> inimigos) {
        if (inimigos == null) {
            return List.of();
        }

        return inimigos.stream()
                .map(BatalhaMapper::toInimigoResponse)
                .toList();
    }

    private static InimigoBatalhaResponse toInimigoResponse(CombatenteSnapshot inimigo) {
        return InimigoBatalhaResponse.builder()
                .id(inimigo.getId())
                .nome(inimigo.getNome())
                .vidaAtual(inimigo.getVidaAtual())
                .vidaMaxima(inimigo.getVidaMaxima())
                .vivo(inimigo.getVidaAtual() != null && inimigo.getVidaAtual() > 0)
                .build();
    }
}
