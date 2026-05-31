package com.rpgturnos.combate.repository;

import com.rpgturnos.combate.model.Inimigo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InimigoRepository extends JpaRepository<Inimigo, Long> {

    List<Inimigo> findByFase(Integer fase);
}
