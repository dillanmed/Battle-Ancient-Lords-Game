package com.rpgturnos.combate.client;

import com.rpgturnos.combate.client.dto.CharacterResponse;

public interface CharacterClient {

    CharacterResponse buscarPersonagemPorId(Long personagemId);
}