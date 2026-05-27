package com.rpgturnos.combate.controller;

import com.rpgturnos.combate.facade.CombateFacade;
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
    public ResponseEntity<Batalha> criarBatalha(@RequestBody Batalha batalha) {

        Batalha novaBatalha = combateFacade.iniciarBatalha(batalha);

        return ResponseEntity.ok(novaBatalha);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Batalha> buscarPorId(@PathVariable Long id) {

        return combateFacade.buscarBatalha(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Batalha>> listarBatalhas() {

        return ResponseEntity.ok(combateFacade.listarBatalhas());
    }

    @PostMapping("/{id}/atacar")
    public ResponseEntity<Batalha> atacar(@PathVariable Long id) {

        Batalha batalha = combateFacade.atacar(id);

        return ResponseEntity.ok(batalha);
    }

    @PostMapping("/{id}/defender")
    public ResponseEntity<Batalha> defender(@PathVariable Long id) {

        Batalha batalha = combateFacade.defender(id);

        return ResponseEntity.ok(batalha);
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Batalha> finalizarBatalha(@PathVariable Long id) {

        Batalha batalha = combateFacade.finalizarBatalha(id);

        return ResponseEntity.ok(batalha);
    }
}