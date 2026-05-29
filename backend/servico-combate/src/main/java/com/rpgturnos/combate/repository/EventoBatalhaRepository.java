package com.rpgturnos.combate.repository;

import com.rpgturnos.combate.model.EventoBatalha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoBatalhaRepository extends JpaRepository<EventoBatalha, Long> {

    List<EventoBatalha> findByBatalhaIdOrderByCriadoEmAsc(Long batalhaId);
}