package com.rpgturnos.combate.controller;

import com.rpgturnos.combate.dto.BatalhaResponse;
import com.rpgturnos.combate.dto.AtaqueRequest;
import com.rpgturnos.combate.dto.CriarBatalhaRequest;
import com.rpgturnos.combate.dto.EventoBatalhaResponse;
import com.rpgturnos.combate.dto.HabilidadeRequest;
import com.rpgturnos.combate.facade.CombateFacade;
import com.rpgturnos.combate.mapper.BatalhaMapper;
import com.rpgturnos.combate.model.Batalha;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/batalhas")
public class BatalhaController {

    private final CombateFacade combateFacade;

    public BatalhaController(CombateFacade combateFacade) {
        this.combateFacade = combateFacade;
    }

    @PostMapping
    public ResponseEntity<BatalhaResponse> criarBatalha(@RequestBody CriarBatalhaRequest request) {
        Batalha novaBatalha = combateFacade.criarBatalha(BatalhaMapper.toEntity(request));
        return ResponseEntity.ok(BatalhaMapper.toResponse(novaBatalha));
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<BatalhaResponse> iniciarBatalha(@PathVariable Long id) {
        Batalha batalha = combateFacade.iniciarBatalha(id);
        return ResponseEntity.ok(BatalhaMapper.toResponse(batalha));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatalhaResponse> buscarPorId(@PathVariable Long id) {
        return combateFacade.buscarBatalha(id)
                .map(BatalhaMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BatalhaResponse>> listarBatalhas() {
        return ResponseEntity.ok(combateFacade.listarBatalhas()
                .stream()
                .map(BatalhaMapper::toResponse)
                .toList());
    }

    @PostMapping("/{id}/atacar")
    public ResponseEntity<BatalhaResponse> atacar(@PathVariable Long id,
                                                  @RequestBody(required = false) AtaqueRequest request) {
        Batalha batalha = combateFacade.atacar(id, request);
        return ResponseEntity.ok(BatalhaMapper.toResponse(batalha));
    }

    @PostMapping("/{id}/defender")
    public ResponseEntity<BatalhaResponse> defender(@PathVariable Long id) {
        Batalha batalha = combateFacade.defender(id);
        return ResponseEntity.ok(BatalhaMapper.toResponse(batalha));
    }

    @PostMapping("/{id}/habilidade")
    public ResponseEntity<BatalhaResponse> usarHabilidade(@PathVariable Long id,
                                                          @RequestBody HabilidadeRequest request) {
        Batalha batalha = combateFacade.usarHabilidade(id, request);
        return ResponseEntity.ok(BatalhaMapper.toResponse(batalha));
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<BatalhaResponse> finalizarBatalha(@PathVariable Long id) {
        Batalha batalha = combateFacade.finalizarBatalha(id);
        return ResponseEntity.ok(BatalhaMapper.toResponse(batalha));
    }

    @GetMapping("/{id}/eventos")
    public ResponseEntity<List<EventoBatalhaResponse>> listarEventos(@PathVariable Long id) {
        return ResponseEntity.ok(combateFacade.listarEventos(id)
                .stream()
                .map(BatalhaMapper::toResponse)
                .toList());
    }
}
