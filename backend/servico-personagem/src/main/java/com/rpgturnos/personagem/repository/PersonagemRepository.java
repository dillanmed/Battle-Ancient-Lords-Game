package com.rpgturnos.personagem.repository;

import com.rpgturnos.personagem.model.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonagemRepository extends JpaRepository<Personagem, Long> {

    List<Personagem> findByUsuarioId(Long usuarioId);
}
