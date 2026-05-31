package com.rpgturnos.combate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.rpgturnos.combate.client.CharacterClient;
import com.rpgturnos.combate.client.DadosCombatePersonagemResponse;
import com.rpgturnos.combate.aspect.ExecutionTimeAspect;
import com.rpgturnos.combate.aspect.LoggingAspect;
import com.rpgturnos.combate.model.TipoEventoBatalha;
import com.rpgturnos.combate.repository.EventoBatalhaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BatalhaFluxoControllerTests {

    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventoBatalhaRepository eventoBatalhaRepository;

    @Autowired
    private LoggingAspect loggingAspect;

    @Autowired
    private ExecutionTimeAspect executionTimeAspect;

    @Test
    void deveExecutarFluxoBasicoDeBatalha() throws Exception {
        Long batalhaId = criarBatalha();

        mockMvc.perform(get("/batalhas/{id}", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/batalhas"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/iniciar", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/atacar", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/habilidade", batalhaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Golpe Arcano",
                                  "tipo": "MAGICA",
                                  "dano": 10,
                                  "custoMana": 5
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/defender", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/finalizar", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/batalhas/{id}/eventos", batalhaId))
                .andExpect(status().isOk());
    }

    @Test
    void deveUsarHabilidadeComSucesso() throws Exception {
        Long batalhaId = criarBatalha();
        iniciarBatalha(batalhaId);

        mockMvc.perform(post("/batalhas/{id}/habilidade", batalhaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Furia",
                                  "tipo": "BUFF_ATAQUE",
                                  "dano": 5,
                                  "custoMana": 4
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void deveRecusarHabilidadeSemMana() throws Exception {
        Long batalhaId = criarBatalha();
        iniciarBatalha(batalhaId);

        mockMvc.perform(post("/batalhas/{id}/habilidade", batalhaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Explosao",
                                  "tipo": "MAGICA",
                                  "dano": 50,
                                  "custoMana": 999
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRecusarHabilidadeComBatalhaFinalizada() throws Exception {
        Long batalhaId = criarBatalha();
        iniciarBatalha(batalhaId);

        mockMvc.perform(post("/batalhas/{id}/finalizar", batalhaId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/batalhas/{id}/habilidade", batalhaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Explosao",
                                  "tipo": "MAGICA",
                                  "dano": 20,
                                  "custoMana": 5
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveSalvarEventoDeHabilidade() throws Exception {
        Long batalhaId = criarBatalha();
        iniciarBatalha(batalhaId);

        mockMvc.perform(post("/batalhas/{id}/habilidade", batalhaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Enfraquecer Armadura",
                                  "tipo": "DEBUFF_DEFESA",
                                  "dano": 3,
                                  "custoMana": 5
                                }
                                """))
                .andExpect(status().isOk());

        boolean eventoSalvo = eventoBatalhaRepository.findByBatalhaIdOrderByCriadoEmAsc(batalhaId)
                .stream()
                .anyMatch(evento -> evento.getTipo() == TipoEventoBatalha.HABILIDADE);

        assertThat(eventoSalvo).isTrue();
    }

    @Test
    void deveCarregarAspectsNoContextoSpring() {
        assertThat(loggingAspect).isNotNull();
        assertThat(executionTimeAspect).isNotNull();
    }

    private Long criarBatalha() throws Exception {
        MvcResult criacao = mockMvc.perform(post("/batalhas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioId": 1,
                                  "personagemId": 10,
                                  "fase": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        return extrairId(criacao);
    }

    private void iniciarBatalha(Long batalhaId) throws Exception {
        mockMvc.perform(post("/batalhas/{id}/iniciar", batalhaId))
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

    @TestConfiguration
    static class CharacterClientTestConfig {

        @Bean
        @Primary
        CharacterClient characterClient() {
            return personagemId -> {
                DadosCombatePersonagemResponse response = new DadosCombatePersonagemResponse();
                response.setId(personagemId);
                response.setNome("Ayla");
                response.setClasse("GUERREIRO");
                response.setNivel(2);
                response.setVidaMaxima(120);
                response.setManaMaxima(40);
                response.setAtaque(30);
                response.setDefesa(12);
                response.setForca(15);
                response.setInteligencia(6);
                response.setAgilidade(8);
                return response;
            };
        }
    }
}
