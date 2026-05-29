package com.rpgturnos.combate.mapper;

import com.rpgturnos.combate.dto.BatalhaResponse;
import com.rpgturnos.combate.dto.CriarBatalhaRequest;
import com.rpgturnos.combate.dto.EventoBatalhaResponse;
import com.rpgturnos.combate.model.Batalha;
import com.rpgturnos.combate.model.CombatenteSnapshot;
import com.rpgturnos.combate.model.EventoBatalha;

public final class BatalhaMapper {

    private BatalhaMapper() {
    }

    public static Batalha toEntity(CriarBatalhaRequest request) {
        CombatenteSnapshot jogador = CombatenteSnapshot.builder()
                .referenciaOriginalId(request.getPersonagemId())
                .nome(valorOuPadrao(request.getNomeJogador(), "Jogador"))
                .tipo("JOGADOR")
                .vidaMaxima(valorOuPadrao(request.getVidaJogador(), 100))
                .vidaAtual(valorOuPadrao(request.getVidaJogador(), 100))
                .manaMaxima(valorOuPadrao(request.getManaJogador(), 50))
                .manaAtual(valorOuPadrao(request.getManaJogador(), 50))
                .ataque(valorOuPadrao(request.getAtaqueJogador(), 20))
                .defesa(valorOuPadrao(request.getDefesaJogador(), 10))
                .nivel(valorOuPadrao(request.getNivelJogador(), 1))
                .build();

        return Batalha.builder()
                .usuarioId(request.getUsuarioId())
                .personagemId(request.getPersonagemId())
                .jogador(jogador)
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

    private static String valorOuPadrao(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private static Integer valorOuPadrao(Integer valor, Integer padrao) {
        return valor == null ? padrao : valor;
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
}
