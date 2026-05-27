package com.rpgturnos.personagem.repository;

import com.rpgturnos.personagem.model.ClassePersonagem;
import com.rpgturnos.personagem.model.Habilidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabilidadeRepository extends JpaRepository<Habilidade, Long> {

    List<Habilidade> findByClassePermitida(ClassePersonagem classePermitida);

    List<Habilidade> findByClassePermitidaAndNivelNecessarioLessThanEqual(ClassePersonagem classePermitida,
                                                                          Integer nivel);

    boolean existsByNomeAndClassePermitida(String nome, ClassePersonagem classePermitida);
}
