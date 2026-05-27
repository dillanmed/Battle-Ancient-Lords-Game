package com.rpgturnos.personagem.controller;

import com.rpgturnos.personagem.dto.AtualizarExperienciaRequest;
import com.rpgturnos.personagem.dto.CriarPersonagemRequest;
import com.rpgturnos.personagem.dto.DadosCombatePersonagemResponse;
import com.rpgturnos.personagem.dto.HabilidadeResponse;
import com.rpgturnos.personagem.dto.PersonagemResponse;
import com.rpgturnos.personagem.service.PersonagemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonagemController {

    private final PersonagemService personagemService;

    public PersonagemController(PersonagemService personagemService) {
        this.personagemService = personagemService;
    }

    @PostMapping("/personagens")
    public ResponseEntity<PersonagemResponse> criar(@Valid @RequestBody CriarPersonagemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personagemService.criar(request));
    }

    @GetMapping("/personagens/{id}")
    public ResponseEntity<PersonagemResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(personagemService.buscarPorId(id));
    }

    @GetMapping("/usuarios/{usuarioId}/personagens")
    public ResponseEntity<List<PersonagemResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(personagemService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/personagens/{id}/habilidades")
    public ResponseEntity<List<HabilidadeResponse>> listarHabilidades(@PathVariable Long id) {
        return ResponseEntity.ok(personagemService.listarHabilidades(id));
    }

    @GetMapping("/personagens/{id}/dados-combate")
    public ResponseEntity<DadosCombatePersonagemResponse> buscarDadosCombate(@PathVariable Long id) {
        return ResponseEntity.ok(personagemService.buscarDadosCombate(id));
    }

    @PutMapping("/personagens/{id}/experiencia")
    public ResponseEntity<PersonagemResponse> atualizarExperiencia(@PathVariable Long id,
                                                                   @Valid @RequestBody AtualizarExperienciaRequest request) {
        return ResponseEntity.ok(personagemService.atualizarExperiencia(id, request));
    }

    @PutMapping("/personagens/{id}/evoluir")
    public ResponseEntity<PersonagemResponse> evoluir(@PathVariable Long id) {
        return ResponseEntity.ok(personagemService.evoluir(id));
    }

    @DeleteMapping("/personagens/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        personagemService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
