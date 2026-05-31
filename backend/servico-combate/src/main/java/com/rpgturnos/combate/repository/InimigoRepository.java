package com.rpgturnos.combate.repository;

import com.rpgturnos.combate.model.Inimigo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InimigoRepository extends JpaRepository<Inimigo, Long> {

    List<Inimigo> findByFase(Integer fase);

    List<Inimigo> findByAtivoTrue();

    List<Inimigo> findByFaseAndAtivoTrue(Integer fase);

    boolean existsByNomeAndFase(String nome, Integer fase);

    Optional<Inimigo> findByNomeAndFase(String nome, Integer fase);
}
