package com.rpgturnos.combate.repository;

import com.rpgturnos.combate.model.Batalha;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatalhaRepository extends JpaRepository<Batalha, Long> {
}