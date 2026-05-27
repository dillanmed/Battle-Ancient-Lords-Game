package com.rpgturnos.personagem.service;

import com.rpgturnos.personagem.dto.AtualizarExperienciaRequest;
import com.rpgturnos.personagem.dto.CriarPersonagemRequest;
import com.rpgturnos.personagem.dto.DadosCombatePersonagemResponse;
import com.rpgturnos.personagem.dto.HabilidadeResponse;
import com.rpgturnos.personagem.dto.PersonagemResponse;
import com.rpgturnos.personagem.exception.PersonagemNotFoundException;
import com.rpgturnos.personagem.factory.PersonagemFactory;
import com.rpgturnos.personagem.model.ClassePersonagem;
import com.rpgturnos.personagem.model.Personagem;
import com.rpgturnos.personagem.repository.PersonagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonagemService {

    private final PersonagemRepository personagemRepository;
    private final HabilidadeService habilidadeService;

    public PersonagemService(PersonagemRepository personagemRepository, HabilidadeService habilidadeService) {
        this.personagemRepository = personagemRepository;
        this.habilidadeService = habilidadeService;
    }

    public PersonagemResponse criar(CriarPersonagemRequest request) {
        Personagem personagem = PersonagemFactory.criar(request.getUsuarioId(), request.getNome(), request.getClasse());
        return PersonagemResponse.from(personagemRepository.save(personagem));
    }

    public PersonagemResponse buscarPorId(Long id) {
        return PersonagemResponse.from(buscarEntidadePorId(id));
    }

    public List<PersonagemResponse> listarPorUsuario(Long usuarioId) {
        return personagemRepository.findByUsuarioId(usuarioId).stream()
                .map(PersonagemResponse::from)
                .toList();
    }

    public List<HabilidadeResponse> listarHabilidades(Long id) {
        Personagem personagem = buscarEntidadePorId(id);
        return habilidadeService.listarDisponiveis(personagem.getClasse(), personagem.getNivel());
    }

    public DadosCombatePersonagemResponse buscarDadosCombate(Long id) {
        Personagem personagem = buscarEntidadePorId(id);
        List<HabilidadeResponse> habilidades = habilidadeService.listarDisponiveis(personagem.getClasse(),
                personagem.getNivel());
        return DadosCombatePersonagemResponse.from(personagem, habilidades);
    }

    public PersonagemResponse atualizarExperiencia(Long id, AtualizarExperienciaRequest request) {
        Personagem personagem = buscarEntidadePorId(id);
        personagem.setExperiencia(personagem.getExperiencia() + request.getExperiencia());
        return PersonagemResponse.from(personagemRepository.save(personagem));
    }

    public PersonagemResponse evoluir(Long id) {
        Personagem personagem = buscarEntidadePorId(id);
        Integer experienciaNecessaria = personagem.getNivel() * 100;

        if (personagem.getExperiencia() >= experienciaNecessaria) {
            personagem.setExperiencia(personagem.getExperiencia() - experienciaNecessaria);
            personagem.setNivel(personagem.getNivel() + 1);
            aplicarAumentoPorClasse(personagem);
            personagem = personagemRepository.save(personagem);
        }

        return PersonagemResponse.from(personagem);
    }

    public void remover(Long id) {
        Personagem personagem = buscarEntidadePorId(id);
        personagemRepository.delete(personagem);
    }

    private Personagem buscarEntidadePorId(Long id) {
        return personagemRepository.findById(id)
                .orElseThrow(() -> new PersonagemNotFoundException(id));
    }

    private void aplicarAumentoPorClasse(Personagem personagem) {
        ClassePersonagem classe = personagem.getClasse();
        personagem.setVidaMaxima(personagem.getVidaMaxima() + 10);
        personagem.setManaMaxima(personagem.getManaMaxima() + 5);
        personagem.setAtaque(personagem.getAtaque() + 2);
        personagem.setDefesa(personagem.getDefesa() + 2);

        switch (classe) {
            case GUERREIRO -> {
                personagem.setVidaMaxima(personagem.getVidaMaxima() + 5);
                personagem.setForca(personagem.getForca() + 3);
                personagem.setDefesa(personagem.getDefesa() + 1);
            }
            case MAGO -> {
                personagem.setManaMaxima(personagem.getManaMaxima() + 10);
                personagem.setInteligencia(personagem.getInteligencia() + 3);
                personagem.setAtaque(personagem.getAtaque() + 1);
            }
            case ARQUEIRO -> {
                personagem.setAgilidade(personagem.getAgilidade() + 3);
                personagem.setForca(personagem.getForca() + 1);
                personagem.setAtaque(personagem.getAtaque() + 1);
            }
        }
    }
}
