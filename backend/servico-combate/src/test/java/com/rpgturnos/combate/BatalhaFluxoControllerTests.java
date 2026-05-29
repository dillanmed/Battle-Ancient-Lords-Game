package com.rpgturnos.combate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BatalhaFluxoControllerTests {

    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveExecutarFluxoBasicoDeBatalha() throws Exception {
        MvcResult criacao = mockMvc.perform(post("/batalhas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioId": 1,
                                  "personagemId": 10,
                                  "nomeJogador": "Ayla",
                                  "vidaJogador": 120,
                                  "manaJogador": 40,
                                  "ataqueJogador": 30,
                                  "defesaJogador": 12,
                                  "nivelJogador": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Long batalhaId = extrairId(criacao);

        mockMvc.perform(get("/batalhas/{id}", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/batalhas"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/iniciar", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/atacar", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/defender", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/finalizar", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/batalhas/{id}/eventos", batalhaId))
                .andExpect(status().isOk());
    }

    private Long extrairId(MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        Matcher matcher = ID_PATTERN.matcher(body);

        if (!matcher.find()) {
            throw new IllegalStateException("Resposta sem id: " + body);
        }

        return Long.valueOf(matcher.group(1));
    }
}
