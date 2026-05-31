package com.rpgturnos.combate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rpgturnos.combate.model.Inimigo;
import com.rpgturnos.combate.repository.InimigoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InimigoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InimigoRepository inimigoRepository;

    @Autowired
    @Qualifier("inicializarInimigos")
    private CommandLineRunner inicializarInimigos;

    @BeforeEach
    void setUp() throws Exception {
        inimigoRepository.deleteAll();
        inicializarInimigos.run();
    }

    @Test
    void deveListarInimigosAtivosPorFase() throws Exception {
        mockMvc.perform(get("/inimigos/fase/{fase}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nome").value("Goblin"))
                .andExpect(jsonPath("$[0].recompensaXp").value(20))
                .andExpect(jsonPath("$[0].ativo").value(true))
                .andExpect(jsonPath("$[0].spriteKey").value("esqueleto"));
    }

    @Test
    void deveListarTodosInimigosAtivos() throws Exception {
        mockMvc.perform(get("/inimigos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(16)))
                .andExpect(jsonPath("$[15].nome").value("Minotauro"))
                .andExpect(jsonPath("$[15].spriteKey").value("Minotauro"))
                .andExpect(jsonPath("$[15].boss").value(true));
    }

    @Test
    void deveBuscarInimigoPorId() throws Exception {
        Inimigo inimigo = inimigoRepository.findByFaseAndAtivoTrue(1).get(0);

        mockMvc.perform(get("/inimigos/{id}", inimigo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inimigo.getId()))
                .andExpect(jsonPath("$.nome").value(inimigo.getNome()));
    }

    @Test
    void dataInitializerNaoDeveDuplicarInimigos() throws Exception {
        long totalInicial = inimigoRepository.count();

        inicializarInimigos.run();

        assertThat(inimigoRepository.count()).isEqualTo(totalInicial);
        assertThat(inimigoRepository.existsByNomeAndFase("Goblin", 1)).isTrue();
        assertThat(inimigoRepository.findByAtivoTrue()).hasSize(16);
    }
}
