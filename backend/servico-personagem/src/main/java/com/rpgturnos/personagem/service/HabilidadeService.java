package com.rpgturnos.personagem.service;

import com.rpgturnos.personagem.dto.HabilidadeResponse;
import com.rpgturnos.personagem.exception.HabilidadeNotFoundException;
import com.rpgturnos.personagem.model.ClassePersonagem;
import com.rpgturnos.personagem.repository.HabilidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabilidadeService {

    private final HabilidadeRepository habilidadeRepository;

    public HabilidadeService(HabilidadeRepository habilidadeRepository) {
        this.habilidadeRepository = habilidadeRepository;
    }

    public List<HabilidadeResponse> listarTodas() {
        return habilidadeRepository.findAll().stream()
                .map(HabilidadeResponse::from)
                .toList();
    }

    public List<HabilidadeResponse> listarPorClasse(ClassePersonagem classe) {
        return habilidadeRepository.findByClassePermitida(classe).stream()
                .map(HabilidadeResponse::from)
                .toList();
    }

    public List<HabilidadeResponse> listarPorClasse(String classe) {
        return listarPorClasse(ClassePersonagem.from(classe));
    }

    public List<HabilidadeResponse> listarDisponiveis(ClassePersonagem classe, Integer nivel) {
        return habilidadeRepository.findByClassePermitidaAndNivelNecessarioLessThanEqual(classe, nivel).stream()
                .map(HabilidadeResponse::from)
                .toList();
    }

    public HabilidadeResponse buscarPorId(Long id) {
        return habilidadeRepository.findById(id)
                .map(HabilidadeResponse::from)
                .orElseThrow(() -> new HabilidadeNotFoundException(id));
    }
}
