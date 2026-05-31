package com.rpgturnos.combate.controller;

import com.rpgturnos.combate.model.Inimigo;
import com.rpgturnos.combate.service.InimigoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inimigos")
public class InimigoController {

    private final InimigoService inimigoService;

    public InimigoController(InimigoService inimigoService) {
        this.inimigoService = inimigoService;
    }

    @GetMapping
    public ResponseEntity<List<Inimigo>> listarInimigos() {
        return ResponseEntity.ok(inimigoService.listarInimigosAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inimigo> buscarPorId(@PathVariable Long id) {
        return inimigoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fase/{fase}")
    public ResponseEntity<List<Inimigo>> buscarInimigosDaFase(@PathVariable Integer fase) {
        return ResponseEntity.ok(inimigoService.buscarInimigosDaFase(fase));
    }
}
