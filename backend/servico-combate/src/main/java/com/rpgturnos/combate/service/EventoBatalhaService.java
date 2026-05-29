package com.rpgturnos.combate.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rpgturnos.combate.model.EventoBatalha;
import com.rpgturnos.combate.model.TipoEventoBatalha;
import com.rpgturnos.combate.repository.EventoBatalhaRepository;

@Service
public class EventoBatalhaService {

    private final EventoBatalhaRepository eventoBatalhaRepository;

    public EventoBatalhaService(EventoBatalhaRepository eventoBatalhaRepository) {
        this.eventoBatalhaRepository = eventoBatalhaRepository;
    }

    public EventoBatalha registrar(Long batalhaId, TipoEventoBatalha tipo, String descricao, Integer round) {
        EventoBatalha evento = EventoBatalha.builder()
                .batalhaId(batalhaId)
                .tipo(tipo)
                .descricao(descricao)
                .round(round)
                .criadoEm(LocalDateTime.now())
                .build();

        return eventoBatalhaRepository.save(evento);
    }

    public List<EventoBatalha> listarPorBatalha(Long batalhaId) {
        return eventoBatalhaRepository.findByBatalhaIdOrderByCriadoEmAsc(batalhaId);
    }
}
