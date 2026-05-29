package com.rpgturnos.combate.client;

import com.rpgturnos.combate.client.dto.CharacterResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CharacterClientImpl implements CharacterClient {

    private final RestTemplate restTemplate;
    

    @Value("${services.character-service.url}")
    private String characterServiceUrl;

    public CharacterClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public CharacterResponse buscarPersonagemPorId(Long personagemId) {
        String url = characterServiceUrl + "/characters/" + personagemId;

        return restTemplate.getForObject(url, CharacterResponse.class);
    }
}