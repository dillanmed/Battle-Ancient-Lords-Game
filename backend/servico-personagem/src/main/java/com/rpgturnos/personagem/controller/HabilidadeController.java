package com.rpgturnos.personagem.controller;

import com.rpgturnos.personagem.dto.HabilidadeResponse;
import com.rpgturnos.personagem.service.HabilidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HabilidadeController {

    private final HabilidadeService habilidadeService;

    public HabilidadeController(HabilidadeService habilidadeService) {
        this.habilidadeService = habilidadeService;
    }

    @GetMapping("/habilidades")
    public ResponseEntity<List<HabilidadeResponse>> listarTodas() {
        return ResponseEntity.ok(habilidadeService.listarTodas());
    }

    @GetMapping("/habilidades/classe/{classe}")
    public ResponseEntity<List<HabilidadeResponse>> listarPorClasse(@PathVariable String classe) {
        return ResponseEntity.ok(habilidadeService.listarPorClasse(classe));
    }

    @GetMapping("/habilidades/{id}")
    public ResponseEntity<HabilidadeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(habilidadeService.buscarPorId(id));
    }
}
